package com.github.dreamroute.mybatis.pro.core.interceptor;

import cn.hutool.core.io.FileUtil;
import com.github.dreamroute.mybatis.pro.sdk.SelectMapper;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * 处理findBy限制列拦截器，findBy方法的最后1个参数如果是列名数组，那么将列名替换掉之前的星号
 *
 * @author : w.dehai.2021.04.01
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class ColumnInterceptor implements Interceptor {

    private static final String ASTERISK = "*";
    private static final Map<String, String> COLS_CACHE = new ConcurrentHashMap<>();
    private static final List<String> BASE_MAPPER_SELECT_METHODS;

    static {
        Method[] selectMethods = SelectMapper.class.getDeclaredMethods();
        BASE_MAPPER_SELECT_METHODS = stream(selectMethods).map(Method::getName).collect(toList());
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        String id = ms.getId();
        String methodName = FileUtil.getSuffix(id);

        if (methodName.startsWith("findBy") || BASE_MAPPER_SELECT_METHODS.contains(methodName)) {
            COLS_CACHE.computeIfAbsent(id, key -> {
                String colums = ASTERISK;
                if (args[1] instanceof ParamMap) {
                    ParamMap<Object> params = (ParamMap<Object>) args[1];
                    if (params.containsKey("cols")) {
                        String[] cols = (String[]) params.get("cols");
                        if (cols != null && cols.length > 0) {
                            String[] cs = (String[]) cols;
                            colums = String.join(", ", cs);
                        }
                    }
                }
                // 对于同一个方法只需要处理一次即可，ms是常驻内存的
                MetaObject mo = SystemMetaObject.forObject(ms);
                SqlSource sqlSource = ms.getSqlSource();
                if (sqlSource instanceof DynamicSqlSource) {
                    List<Object> contents = (List<Object>) mo.getValue("sqlSource.rootSqlNode.contents");
                    StaticTextSqlNode stsn = (StaticTextSqlNode) contents.get(0);
                    MetaObject stsnMo = SystemMetaObject.forObject(stsn);
                    String sql = (String) stsnMo.getValue("text");
                    sql = sql.replace(ASTERISK, colums);
                    stsnMo.setValue("text", sql);
                    contents.set(0, stsn);
                } else if (sqlSource instanceof RawSqlSource) {
                    String sql = (String) mo.getValue("sqlSource.sqlSource.sql");
                    sql = sql.replace(ASTERISK, colums);
                    mo.setValue("sqlSource.sqlSource.sql", sql);
                }
                return colums;
            });
        }
        return invocation.proceed();
    }
}
