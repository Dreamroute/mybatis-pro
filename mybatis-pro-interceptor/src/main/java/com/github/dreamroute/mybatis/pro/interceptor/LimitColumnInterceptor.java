package com.github.dreamroute.mybatis.pro.interceptor;

import cn.hutool.core.io.FileUtil;
import com.github.dreamroute.mybatis.pro.core.consts.MyBatisProProperties;
import com.github.dreamroute.mybatis.pro.core.exception.MyBatisProException;
import com.github.dreamroute.mybatis.pro.core.util.MyBatisProUtil;
import com.github.dreamroute.mybatis.pro.core.util.SqlUtil;
import com.github.dreamroute.mybatis.pro.sdk.SelectMapper;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
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

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.dreamroute.mybatis.pro.core.consts.LogicalDeleteType.UPDATE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * 插件功能：
 * 1、拦截mybatis-pro内置查询方法(也就是：SelectMapper接口的方法)以及findBy打头的方法，如果这些方法传参数有指定列名，那么将指定列名替换掉[select * from]中的"*"号，如果没有指定列名，那么用全部列替换星号。
 * 特殊说明：由于同一个方法名可能传入的限制列不同，如果将列写死在ms的sql中，处出现冲突（只有第一个会生效），所以这里必须使用插件的方式将每个不同的方法（方法名+参数列表）进行缓存
 *
 * 2、如果，使用update方式的逻辑删除，那么在特殊方法的sql末尾追加状态条件
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
    // findBy + base，且不是existBy和countBy
    private static final Map<String, Boolean> FIND_OR_BASE_ID_CACHE = new ConcurrentHashMap<>();
    // findBy + existBy + countBy + base
    private static final Map<String, Boolean> EXIST_OR_COUNT_BASE_ID_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> COLS_ALIAS = new HashMap<>();

    private Configuration configuration;

    @Resource
    private MyBatisProProperties props;

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

        Boolean findByCache = FIND_OR_BASE_ID_CACHE.computeIfAbsent(id, k -> {
            String methodName = FileUtil.getSuffix(k);
            // start with findBy or existBy base select methods.
            return MyBatisProUtil.isFindBy(methodName) || SELECT_MAPPER_METHOD_NAMES.contains(methodName);
        });

        Boolean existByOrCountByCache = EXIST_OR_COUNT_BASE_ID_CACHE.computeIfAbsent(id, k -> {
            String methodName = FileUtil.getSuffix(k);
            // start with findBy or existBy base select methods.
            return MyBatisProUtil.isExistByOrCountByMethod(methodName) || SELECT_MAPPER_METHOD_NAMES.contains(methodName);
        });


        BoundSql boundSql = (BoundSql) mo.getValue("delegate.boundSql");
        String sql = null;
        if (Boolean.TRUE.equals(findByCache)) {
            Class<?> returnType = ms.getResultMaps().get(0).getType();
            Map<String, String> alias = MyBatisProUtil.FIELDS_ALIAS_CACHE.get(returnType);

            String cols = getCols(id, boundSql, alias);
            cols = cols == null ? toColumns(alias.keySet(), alias) : cols;

            sql = boundSql.getSql();

            // 替换星号
            sql = sql.replace(ASTERISK, cols);
        } else if (TRUE.equals(existByOrCountByCache)) {
            sql = boundSql.getSql();
        }

        if (findByCache || existByOrCountByCache) {
            String resultSql = appendState(sql);
            configuration.newMetaObject(boundSql).setValue("sql", resultSql);
        }

        return invocation.proceed();
    }

    // 追加逻辑删除的状态
    private String appendState(String sql) {
        // 如果逻辑删除是update方式，那么在sql结尾追加（状态 = 有效）
        if (props.getLogicalDeleteType() == UPDATE) {
            try {
                Select select = (Select) CCJSqlParserUtil.parse(sql);
                PlainSelect body = (PlainSelect) select.getSelectBody();
                Expression where = body.getWhere();
                // where为空时追加一个状态条件
                EqualsTo state = new EqualsTo(new Column(props.getLogicalDeleteColumn()), new LongValue(props.getLogicalDeleteActive()));
                if (where == null) {
                    body.setWhere(state);
                }
                // where不为空时，由于存在多个组合查询条件，将状态条件放在任何地方都不合适，因为可能会改变原有sql的含义，所以需要将原有条件用小括号包含然后和状态做与运算
                else {
                    // (原) and state = xxx
                    Parenthesis p = new Parenthesis(where);
                    AndExpression newWhere = new AndExpression().withLeftExpression(p).withRightExpression(state);
                    body.setWhere(newWhere);
                }
                return select.toString();
            } catch (JSQLParserException e) {
                throw new MyBatisProException("SQL: " + sql + "格式有误");
            }
        }
        return sql;
    }

    private String getCols(String id, BoundSql boundSql, Map<String, String> alias) {
        /*
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
         * 事实上：mybatis真是的固定对象是MappedStatement，每次的查询都是从一个MappedStatement开始的，后续的都是新创建的对象，只要不破坏MappedStatement的结构，基本上不会引发并发问题，
         * 观察mybatis的查询流程，如果不考虑一级缓存的情况，其实真正的查库是从{@link org.apache.ibatis.executor.SimpleExecutor#doQuery}开始的，逻辑是：
         * 1. 使用configuration创建StatementHandler
         * 2. 调用prepareStatement方法
         * 3. StatementHandler调用query方法
         * 所以，编写插件如果需要新的查询，那么创建一个StatementHandler即可。
         *
         * 综上所述，编写插件时，对于新的查询，步骤是：
         * 1. 获取旧的BoundSql
         * 2. 创建新的BoundSql
         *  2.1 对于创建BoundSql有两种方式，一种是直接创建，一种是创建SqlSource（比如StaticSqlSource）然后调用getBoundSql
         *  2.2. 这里不能直接使用第二种，因为如果是DyanamicSqlSource创建出来的SqlSource是带有AddtionalParameter的，而StaticSqlSouce就没有
         *  2.3. 如果要使用第二种，依然需要把原来的BoundSql中的AdditionalParameter复制到新的BoundSql中
         *  2.4. 并且如果使用第二种，在创建StaticSqlSource时需要使用带有parameterMappings参数的构造方法，否则就不用，因为parameterMappings参数终归目的是写入到BoundSql中
         * 3. 将旧的BoundSql属性复制到新的里面（如果参数不变的话）
         * 4. 处理BoundSql中的ParameterMappings，如果有变化的话，需要新增
         * 5. 创建StaticSqlSource
         * 6. 创建MappedStatement（需要注意ms的id，因为sqlprinter打印sql是根据id来的，注意是否需要与元id相同）
         *  6.1 特别注意：因为此时的SqlSource是固定的StaticSqlSource，里面的foreach已经被处理过了，问号参数个数已经确定，所以不能缓存ms，必须每次使用新的
         *  6.2 由于6.1的特性以及插件里面使用了一些反射，所以引入插件势必会造成mybatis性能的下降，这是便捷与性能之间的折中
         *  6.3 如果使用原始的SqlSource而不使用StaticSqlSource，主要是针对DynamicSqlSource，RawSqlSource不受影响。那么就可以缓存ms。
         * 7. 获取Executor
         * 8. 使用configuration构建StatementHandler
         * 9. 调用prepareStatement方法
         * 10. StatementHandler调用query方法
         *
         * // 带缓存ms的查询
         * 问题描述：由于上述方法构建的BoundSql中的sql是固定的，而带有foreach的sql语句中的问号个数是动态的，所有就无法缓存ms，原因是采用了固定的StaticSqlSource
         * 解决方案：在构建ms的时候使用原始的SqlSource，在拼接上需要的sql片段（针对dynamicsqlsource方式，raw方式不存在这个问题）
         *
         * **重点：如果要修改（比如增加）List<ParameterMapping>的元素，一定要新建一个列表，例如：
         * <pre>
         *         // 不能使用下方这种方式，每次ms.getBoundSql(param)时会创建一个boundSql，而BoundSql里面的List<ParameterMapping>来自sqlSource（DynamicSqlSource，RawSqlSource调用getBoundSql是相同的原理）
         *         // 而sqlSource是固化在ms里面的，所以如果直接用下方的方式add(vpm)，那么就相当于改变了sqlSource，也就是改变了ms，会出现并发问题
         *         // List<ParameterMapping> pms = boundSql.getParameterMappings();
         *         // pms.add(vpm);
         * </pre>
         */

        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject instanceof MapperMethod.ParamMap) {
            @SuppressWarnings("unchecked") MapperMethod.ParamMap<Object> params = (MapperMethod.ParamMap<Object>) parameterObject;
            if (params.containsKey(COLS)) {
                String[] cols = (String[]) params.get(COLS);
                if (cols != null && cols.length > 0) {
                    String cl = String.join(",", cols);
                    return COLS_ALIAS.computeIfAbsent(id + "#" + cl, k -> toColumns(Arrays.asList(cols), alias));
                }
            }
            return getAllFields(id, alias);
        } else {
            return getAllFields(id, alias);
        }
    }

    private String getAllFields(String id, Map<String, String> alias) {
        return COLS_ALIAS.computeIfAbsent(id + "#" + String.join(",", alias.keySet()), k -> toColumns(alias.keySet(), alias));
    }

    private String toColumns(Collection<String> cols, Map<String, String> alias) {
        return cols.stream().map(fieldName -> {
            String as = alias.get(fieldName);
            boolean mapUnderscoreToCamelCase = this.configuration.isMapUnderscoreToCamelCase();
            String toLine = mapUnderscoreToCamelCase ? SqlUtil.toLine(fieldName) : fieldName;
            return Objects.equals(as, fieldName) ? toLine : (as + " AS " + fieldName);
        }).collect(joining(", "));
    }

}
