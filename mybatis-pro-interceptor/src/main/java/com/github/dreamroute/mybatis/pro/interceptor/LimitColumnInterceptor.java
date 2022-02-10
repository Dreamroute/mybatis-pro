package com.github.dreamroute.mybatis.pro.interceptor;

import cn.hutool.core.io.FileUtil;
import com.github.dreamroute.mybatis.pro.core.consts.MyBatisProProperties;
import com.github.dreamroute.mybatis.pro.core.util.MyBatisProUtil;
import com.github.dreamroute.mybatis.pro.core.util.SqlUtil;
import com.github.dreamroute.mybatis.pro.sdk.SelectMapper;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * 插件功能：
 * 拦截mybatis-pro内置查询方法(也就是：SelectMapper接口的方法)以及findBy打头的方法，如果这些方法传参数有指定列名，那么将指定列名替换掉[select * from]中的"*"号，如果没有指定列名，那么用全部列替换星号。
 * 特殊说明：由于同一个方法名可能传入的限制列不同，如果将列写死在ms的sql中，处出现冲突（只有第一个会生效），所以这里必须使用插件的方式将每个不同的方法（方法名+参数列表）进行缓存
 *
 * @author : w.dehai.2021.04.01
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
@ConditionalOnBean(SqlSessionFactory.class)
@EnableConfigurationProperties({MybatisProperties.class, MyBatisProProperties.class})
public class LimitColumnInterceptor implements Interceptor, ApplicationListener<ContextRefreshedEvent> {

    private static final String ASTERISK = "*";
    private static final String COLS = "cols";

    private static final List<String> SELECT_MAPPER_METHOD_NAMES = stream(SelectMapper.class.getDeclaredMethods()).map(Method::getName).collect(toList());
    private static final Map<String, Boolean> ID_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> COLS_ALIAS = new HashMap<>();

    private Configuration configuration;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        configuration = event.getApplicationContext().getBean(SqlSessionFactory.class).getConfiguration();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler sh = (StatementHandler) ProxyUtil.getOriginObj(invocation.getTarget());
        MetaObject mo = configuration.newMetaObject(sh);

        MappedStatement ms = (MappedStatement) mo.getValue("delegate.mappedStatement");
        String id = ms.getId();
        Boolean cached = ID_CACHE.computeIfAbsent(id, k -> {
            String methodName = FileUtil.getSuffix(k);
            // start with findBy or base select methods.
            return MyBatisProUtil.isFindByMethod(methodName) || SELECT_MAPPER_METHOD_NAMES.contains(methodName);
        });

        if (Boolean.TRUE.equals(cached)) {
            Class<?> returnType = ms.getResultMaps().get(0).getType();
            Map<String, String> alias = MyBatisProUtil.FIELDS_ALIAS_CACHE.get(returnType);

            BoundSql boundSql = (BoundSql) mo.getValue("delegate.boundSql");
            String cols = getCols(id, boundSql, alias);
            cols = cols == null ? toColumns(alias.keySet(), alias) : cols;

            String sql = boundSql.getSql();

            // 替换星号
            sql = sql.replace(ASTERISK, cols);
            configuration.newMetaObject(boundSql).setValue("sql", sql);
        }

        return invocation.proceed();
    }

    private String getCols(String id, BoundSql boundSql, Map<String, String> alias) {
        Object parameterObject = boundSql.getParameterObject();
        /**
         * mybatis封装参数的规则是：{@link MapperMethod#convertArgsToSqlCommandParam}
         * 1. 如果只有1个参数，那么取出参数，然后使用wrapToMapIfCollection包裹
         * 2. 如果多个参数，那么使用ParamMap来存，k就是参数的名称（jdk8+才支持的特性）：
         * <pre>
         *     {
         *         "k1": v1,
         *         "k2": v2,
         *         "param1": v1,
         *         "param2": v2
         *     }
         * </pre>
         *
         * 关于sqlsource参考这里：https://www.imooc.com/article/319115
         * 1. DynamicSqlSource：针对动态 SQL 和 ${} 占位符的 SQL
         * 2. RawSqlSource：针对 #{}占位符的 SQL
         * 3. ProviderSqlSource：针对 @*Provider 注解 提供的 SQL
         * 4. StaticSqlSource：仅包含有 ?占位符的 SQL
         *
         * 对于SqlSource的创建，使用的是{@link org.apache.ibatis.scripting.xmltags.XMLScriptBuilder#parseScriptNode}方法，此方法的返回值只有2种，一种是DynamicSqlSource，另一种是RawSqlSource（内部包装了一个StaticSqlSource），
         * 而对于这两种（其实共有4中，除了这两种还有StaticSqlSource和ProviderSqlSource（不常见））SqlSource，调用getBoundSql得到的BoundSql都是相同的，都是得到了带有问号"?"的sql，所以对于编写插件而言，可以使用BoundSql的getSql()方法，
         * 只是需要注意将BoundSql的属性补全，用于处理含有in的sql，方法类似如下：
         * <pre>
         *     private static void copyProps(BoundSql oldBs, BoundSql newBs, Configuration config) {
         *         MetaObject oldMo = config.newMetaObject(oldBs);
         *         Object ap = oldMo.getValue("additionalParameters");
         *         Object mp = oldMo.getValue("metaParameters");
         *
         *         MetaObject newMo = config.newMetaObject(newBs);
         *         newMo.setValue("additionalParameters", ap);
         *         newMo.setValue("metaParameters", mp);
         *     }
         * </pre>
         *
         * 对于mybatis而言：每一次查询都会使用MappedStatement创建一个新的BoundSql，方法在：{@link org.apache.ibatis.executor.CachingExecutor#query}，而MappedStatement不会被改变，因此如果编写插件时如果修改了MappedStatement
         * 是不行的，会引起并发问题，而对于BoundSql可以随意修改，BoundSql从上述方法被创建之后会一直持续到查询结束，并且在被创建之后的后续执行中，会被写入到StatementHandler中，因此编写插件时，如果拦截的是StatementHandler，那么可以直接
         * 从StatementHandler中获取原始BoundSql，而对于在插件中需要新建BoundSql的场景，只需要注意把BoundSql中的参数填充满即可
         *
         * 事实上：mybatis真是的固定对象是MappedStatement，每次的查询都是从一个MappedStatement开始的，后续的都是新创建的对象，只要不破坏MappedStatement的结构，基本上不会引发并发问题
         */
        if (parameterObject instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap<Object> params = (MapperMethod.ParamMap<Object>) parameterObject;
            if (params.containsKey(COLS)) {
                String[] cols = (String[]) params.get(COLS);
                if (cols != null && cols.length > 0) {
                    String cl = String.join(", ", cols);
                    return COLS_ALIAS.computeIfAbsent(id + "#" + cl, k -> toColumns(Arrays.asList(cols), alias));
                }
            }
        }
        return null;
    }

    private String toColumns(Collection<String> cols, Map<String, String> alias) {
        return cols.stream().map(fieldName -> {
            String as = alias.get(fieldName);
            boolean mapUnderscoreToCamelCase = this.configuration.isMapUnderscoreToCamelCase();
            String toLine = mapUnderscoreToCamelCase ? SqlUtil.toLine(fieldName) : fieldName;
            return isEmpty(as) ? toLine : (as + " AS " + fieldName);
        }).collect(joining(", "));
    }

}
