package com.github.dreamroute.mybatis.pro.core.interceptor;

import cn.hutool.core.io.FileUtil;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
 * 处理findBy限制列拦截器，findBy方法的最后1个参数如果是列名数组，那么将列名替换掉之前的星号，
 * 由于同一个方法名可能传入的限制列不同，如果将列写死在ms的sql中，那么会有并发问题，所以这里必须使用插件的方式每次进行动态替换列
 *
 * @author : w.dehai.2021.04.01
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
@ConditionalOnBean(SqlSessionFactory.class)
public class LimitColumnInterceptor implements Interceptor, ApplicationListener<ContextRefreshedEvent> {

    private static final String ASTERISK = "*";
    private static final String COLS = "cols";


    private static final ConcurrentHashMap<String, Boolean> ID_CACHE = new ConcurrentHashMap<>();
    private static final List<String> BASE_MAPPER_SELECT_METHODS;
    private static final Map<String, String> COLS_ALIAS = new HashMap();

    static {
        Method[] selectMethods = SelectMapper.class.getDeclaredMethods();
        BASE_MAPPER_SELECT_METHODS = stream(selectMethods).map(Method::getName).collect(toList());
    }

    private Configuration configuration;

    @Value("${mybatis.configuration.map-underscore-to-camel-case:false}")
    private boolean underscoreToCamel;

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
            return isFindByMethod(methodName) || BASE_MAPPER_SELECT_METHODS.contains(methodName);
        });
        if (Boolean.FALSE.equals(cached))
            return invocation.proceed();

        Class<?> returnType = ms.getResultMaps().get(0).getType();
        Map<String, String> alias = FIELDS_ALIAS_CACHE.get(returnType);

        BoundSql boundSql = (BoundSql) mo.getValue("delegate.boundSql");
        String cols = existCols(id, boundSql, alias);
        cols = cols == null ? toColumns(alias.keySet(), alias) : cols;

        String sql = boundSql.getSql();

        sql = sql.replace(ASTERISK, cols);
        configuration.newMetaObject(boundSql).setValue("sql", sql);
        return invocation.proceed();
    }

    private String existCols(String id, BoundSql boundSql, Map<String, String> alias) {
        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject instanceof ParamMap) {
            ParamMap<Object> params = (ParamMap<Object>) parameterObject;
            if (params.containsKey(COLS)) {
                String[] cols = (String[]) params.get(COLS);
                if (cols != null && cols.length > 0) {
                    String cl = String.join(",", cols);
                    return COLS_ALIAS.computeIfAbsent(id + "#" + cl, k -> toColumns(Arrays.asList(cols), alias));
                }
            }
        }
        return null;
    }

    private String toColumns(Collection<String> cols, Map<String, String> alias) {
        return cols.stream().map(filedName -> underscoreToCamel ? toLine(filedName) : filedName).map(fieldName -> {
            String as = alias.get(fieldName);
            return isEmpty(as) ? fieldName : (as + " AS " + fieldName);
        }).collect(joining(", "));
    }

}
