package com.github.dreamroute.mybatis.pro.core.interceptor;

import cn.hutool.core.io.FileUtil;
import com.github.dreamroute.mybatis.pro.core.consts.MyBatisProProperties;
import com.github.dreamroute.mybatis.pro.sdk.SelectMapper;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
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
import java.util.concurrent.ConcurrentHashMap;

import static com.github.dreamroute.mybatis.pro.core.util.MyBatisProUtil.FIELDS_ALIAS_CACHE;
import static com.github.dreamroute.mybatis.pro.core.util.MyBatisProUtil.isFindByMethod;
import static com.github.dreamroute.mybatis.pro.core.util.SqlUtil.toLine;
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
    private static final ConcurrentHashMap<String, Boolean> ID_CACHE = new ConcurrentHashMap<>();
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
            return isFindByMethod(methodName) || SELECT_MAPPER_METHOD_NAMES.contains(methodName);
        });

        if (Boolean.FALSE.equals(cached)) {
            return invocation.proceed();
        }

        Class<?> returnType = ms.getResultMaps().get(0).getType();
        Map<String, String> alias = FIELDS_ALIAS_CACHE.get(returnType);

        BoundSql boundSql = (BoundSql) mo.getValue("delegate.boundSql");
        String cols = getCols(id, boundSql, alias);
        cols = cols == null ? toColumns(alias.keySet(), alias) : cols;

        String sql = boundSql.getSql();

        // 替换星号
        sql = sql.replace(ASTERISK, cols);
        configuration.newMetaObject(boundSql).setValue("sql", sql);
        return invocation.proceed();
    }

    private String getCols(String id, BoundSql boundSql, Map<String, String> alias) {
        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject instanceof ParamMap) {
            ParamMap<Object> params = (ParamMap<Object>) parameterObject;
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
            String toLine = mapUnderscoreToCamelCase ? toLine(fieldName) : fieldName;
            return isEmpty(as) ? toLine : (as + " AS " + fieldName);
        }).collect(joining(", "));
    }

}
