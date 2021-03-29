package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dreamroute.mybatis.pro.core.consts.DbDriver;
import com.github.dreamroute.mybatis.pro.core.exception.MyBatisProException;
import lombok.SneakyThrows;

import javax.sql.DataSource;

import static java.util.Locale.ENGLISH;

/**
 * @author : w.dehai.2021.03.29
 */
public class DriverUtil {

    @SneakyThrows
    public static DbDriver getDriver(DataSource ds){
        String driver = ds.getConnection().getMetaData().getDriverName().toUpperCase(ENGLISH);
        if (driver.contains("MYSQL")) {
            return DbDriver.MYSQL;
        } else if (driver.contains("SQL SERVER")) {
            return DbDriver.SQLSERVER;
        } else if (driver.contains("ORACLE")) {
            return DbDriver.H2;
        }
        throw new MyBatisProException("不兼容的数据库类型");
    }

}
