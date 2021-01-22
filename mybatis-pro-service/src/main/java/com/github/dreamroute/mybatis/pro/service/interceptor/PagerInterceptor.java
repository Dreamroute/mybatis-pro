package com.github.dreamroute.mybatis.pro.service.interceptor;

import com.github.dreamroute.mybatis.pro.service.adaptor.page.PageRequest;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.apache.ibatis.mapping.SqlCommandType.SELECT;

@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class PagerInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object param = args[1];

        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        // 非select不处理
        if (sqlCommandType != SELECT) {
            return invocation.proceed();
        }

        if (!(param instanceof PageRequest)) {
            return invocation.proceed();
        }

        Configuration config = ms.getConfiguration();
        BoundSql boundSql = ms.getBoundSql(param);
        String sql = boundSql.getSql();
        String countSql = "select count(*) c from (" + sql + ") t";
        Executor executor = (Executor) (invocation.getTarget());
        Transaction transaction = executor.getTransaction();
        Connection conn = transaction.getConnection();
        PreparedStatement ps = conn.prepareStatement(countSql);
        BoundSql countBoundSql = new BoundSql(config, countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        ParameterHandler parameterHandler = config.newParameterHandler(ms, boundSql.getParameterObject(), countBoundSql);
        parameterHandler.setParameters(ps);
        ResultSet rs = ps.executeQuery();
        PageContainer<Object> resp = new PageContainer<>();
        while (rs.next()) {
            long totle = rs.getLong("c");
            resp.setTotal(totle);
        }
        ps.close();

        PageRequest<?> pr = (PageRequest<?>) param;
        int pageNum = pr.getPageNum();
        int pageSize = pr.getPageSize();

        // 由于不希望在pageRequest中增加start参数，所以limit时改变pageNum来代替start，因此resp的pageNum需要在设置start之前进行设置
        resp.setPageNum(pageNum);
        resp.setPageSize(pr.getPageSize());

        int start = (pageNum - 1) * pageSize;
        pr.setPageNum(start);

        sql = "select * from (" + sql + ") limit ?, ?";
        MetaObject moms = config.newMetaObject(ms);
        moms.setValue("sqlSource.sqlSource.sql", sql);

        ParameterMapping startMapping = new ParameterMapping.Builder(config, "pageNum", int.class).build();
        ParameterMapping limitMapping = new ParameterMapping.Builder(config, "pageSize", int.class).build();
        boundSql.getParameterMappings().add(startMapping);
        boundSql.getParameterMappings().add(limitMapping);

        Object result = invocation.proceed();
        List<?> ls = (List<?>) result;
        resp.addAll(ls);

        return resp;
    }
}