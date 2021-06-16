package com.github.dreamroute.mybatis.pro.core.interceptor;

import com.github.dreamroute.mybatis.pro.core.consts.MyBatisProProperties;
import com.github.dreamroute.mybatis.pro.sdk.DeleteMapper;
import lombok.AllArgsConstructor;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.lang.reflect.Method;
import java.util.List;

import static com.github.dreamroute.mybatis.pro.core.consts.MyBatisProProperties.DEFAULT_LOGICAL_DELETE_TYPE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * 逻辑删除插件
 *
 * @author w.dehai
 */
@AllArgsConstructor
@Intercepts(@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}))
public class LogicalDeleteInterceptor implements Interceptor {

    private final MyBatisProProperties props;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        List<String> methodNames = asList(DeleteMapper.class.getDeclaredMethods()).stream().map(Method::getName).collect(toList());

        if (props.getLogicalDeleteType().equalsIgnoreCase(DEFAULT_LOGICAL_DELETE_TYPE)) {

        } else {
            Statement stmt = CCJSqlParserUtil.parse("SELECT * FROM tab1");
        }
        Object proceed = invocation.proceed();
        System.err.println(proceed);
        return proceed;
    }
}
