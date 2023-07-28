package com.github.dreamroute.mybatis.pro.interceptor;

import com.github.dreamroute.mybatis.pro.base.codec.enums.JsonUtil;
import com.github.dreamroute.mybatis.pro.core.consts.LogicalDeleteType;
import com.github.dreamroute.mybatis.pro.core.consts.MyBatisProProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.currentTimeMillis;
import static org.apache.ibatis.mapping.SqlCommandType.DELETE;

/**
 * 逻辑删除插件
 *
 * @author w.dehai
 */
@Slf4j
@AllArgsConstructor
@Intercepts(@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}))
public class LogicalDeleteInterceptor implements Interceptor {

    private final MyBatisProProperties props;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (!(props.isEnableLogicalDelete() && Objects.equals(sqlCommandType, DELETE))) {
            return invocation.proceed();
        }

        Executor executor = (Executor) (ProxyUtil.getOriginObj(invocation.getTarget()));
        Transaction transaction = executor.getTransaction();

        Configuration config = ms.getConfiguration();
        Object parameter = args[1];
        BoundSql boundSql = ms.getBoundSql(parameter);
        String sql = boundSql.getSql();
        Delete delete = (Delete) CCJSqlParserUtil.parse(sql);
        String tableName = delete.getTable().getName();

        if (props.getLogicalDeleteType() == LogicalDeleteType.BACKUP) {
            /*
             * 原理：
             * 1. 查询出需要删除的数据；
             * 2. 将此数据存入备份表；
             * 3. 物理删除对应数据
             */
            String selectSql = new StringJoiner(" ").add("SELECT").add("*").add("FROM").add(tableName).add("WHERE").add(delete.getWhere().toString()).toString();
            BoundSql selectBoundSql = new BoundSql(config, selectSql, boundSql.getParameterMappings(), parameter);
            MappedStatement m = new Builder(config, "com.[plugin]mybatis_pro_backup._inner_select", new StaticSqlSource(config, selectSql), SqlCommandType.SELECT).build();
            StatementHandler handler = config.newStatementHandler(executor, m, parameter, RowBounds.DEFAULT, null, selectBoundSql);
            Statement stmt = prepareStatement(transaction, handler);
            ((PreparedStatement) stmt).execute();
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            List<Map<String, Object>> result = newArrayList();
            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<>();
                for (int i = 1; i < columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));
                }
                result.add(rowData);
            }
            stmt.close();

            if (!CollectionUtils.isEmpty(result)) {
                String insert = new StringJoiner(" ").add("INSERT INTO").add(MyBatisProProperties.LOGICAL_DELETE_TABLE_NAME).add("(table_name, data, delete_time) VALUES (?, ?, ?)").toString();
                Connection conn = transaction.getConnection();
                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    for (Map<String, Object> data : result) {
                        ps.setObject(1, tableName);
                        ps.setObject(2, JsonUtil.toJsonStr(data));
                        ps.setTimestamp(3, new Timestamp(currentTimeMillis()));
                        log.info("逻辑删除数据备份SQL: " + ps);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            return invocation.proceed();
        } else if (props.getLogicalDeleteType() == LogicalDeleteType.UPDATE) {
            /*
             * 原理：将delete改成update
             */
            String updateSql = "UPDATE " + tableName + " SET " + props.getLogicalDeleteColumn() + " = " + props.getLogicalDeleteInActive() + " WHERE " + delete.getWhere().toString();
            BoundSql updateBoundSql = new BoundSql(config, updateSql, boundSql.getParameterMappings(), parameter);
            StatementHandler handler = config.newStatementHandler(executor, ms, parameter, RowBounds.DEFAULT, null, updateBoundSql);
            PreparedStatement stmt = (PreparedStatement) prepareStatement(transaction, handler);
            int result = stmt.executeUpdate();
            stmt.close();
            return result;
        }

        throw new IllegalArgumentException("配置文件mybatis.pro.logical-delete-type取值只能是: [backup, update]之一，默认是: backup");
    }

    private Statement prepareStatement(Transaction transaction, StatementHandler handler) throws SQLException {
        Statement stmt = handler.prepare(transaction.getConnection(), transaction.getTimeout());
        handler.parameterize(stmt);
        return stmt;
    }
}
