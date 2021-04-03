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
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.github.dreamroute.mybatis.pro.core.util.MyBatisProUtil.FIELDS_ALIAS_CACHE;
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
public class LimitColumnInterceptor implements Interceptor {

    private static final String ASTERISK = "*";
    private static final List<String> BASE_MAPPER_SELECT_METHODS;
    private static final String FIND_BY = "findBy";
    private static final String COLS = "cols";

    @Value("${mybatis.configuration.map-underscore-to-camel-case:false}")
    private boolean underscoreToCamel;

    static {
        Method[] selectMethods = SelectMapper.class.getDeclaredMethods();
        BASE_MAPPER_SELECT_METHODS = stream(selectMethods).map(Method::getName).collect(toList());
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler sh = (StatementHandler) ProxyUtil.getOriginObj(invocation.getTarget());
        MetaObject mo = SystemMetaObject.forObject(sh);

        MappedStatement ms = (MappedStatement) mo.getValue("delegate.mappedStatement");
        String id = ms.getId();
        String methodName = FileUtil.getSuffix(id);
        if (!(methodName.startsWith(FIND_BY) || BASE_MAPPER_SELECT_METHODS.contains(methodName))) {
            return invocation.proceed();
        }

        Class<?> type = ms.getResultMaps().get(0).getType();
        Map<String, String> alias = FIELDS_ALIAS_CACHE.get(type);

        BoundSql boundSql = (BoundSql) mo.getValue("delegate.boundSql");
        String cols = existCols(boundSql, alias);
        cols = cols == null ? toColumns(alias.keySet(), alias) : cols;

        String sql = boundSql.getSql();

        sql = sql.replace(ASTERISK, cols);
        SystemMetaObject.forObject(boundSql).setValue("sql", sql);
        return invocation.proceed();
    }

    private String existCols(BoundSql boundSql, Map<String, String> alias) {
        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject instanceof ParamMap) {
            ParamMap<Object> params = (ParamMap<Object>) parameterObject;
            if (params.containsKey(COLS)) {
                String[] cols = (String[]) params.get(COLS);
                if (cols != null && cols.length > 0) {
                    return toColumns(Arrays.asList(cols), alias);
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
