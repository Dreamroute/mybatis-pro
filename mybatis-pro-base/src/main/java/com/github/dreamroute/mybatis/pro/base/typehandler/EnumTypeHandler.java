package com.github.dreamroute.mybatis.pro.base.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 枚举type handler处理器
 *
 * @author w.dehai
 */
public class EnumTypeHandler<E extends Enum<?> & EnumMarker> extends BaseTypeHandler<EnumMarker> {

    private Class<E> type;

    public EnumTypeHandler() {}

    public EnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EnumMarker parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter.getValue());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        } else {
            try {
                return EnumMarker.valueOf(type, value);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Cannot convert " + value + " to " + type.getSimpleName() + " by ordinal value.", ex);
            }
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        } else {
            try {
                return EnumMarker.valueOf(type, value);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Cannot convert " + value + " to " + type.getSimpleName() + " by ordinal value.", ex);
            }
        }
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        throw new IllegalArgumentException("MyBatisPro enum typehandler not support CallableStatement.");
    }
}