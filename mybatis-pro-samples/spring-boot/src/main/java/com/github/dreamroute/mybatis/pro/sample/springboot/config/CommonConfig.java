package com.github.dreamroute.mybatis.pro.sample.springboot.config;

import com.github.dreamroute.sqlprinter.interceptor.SqlPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author w.dehai
 *
 */
@Configuration
public class CommonConfig {

    @Value("${sql-show:true}")
    private String show;

    /**
     * SQL打印，将sql中的问号（？）替换成真实值
     */
    @Bean
    public SqlPrinter printer() {
        SqlPrinter printer = new SqlPrinter();
        Properties props = new Properties();
        props.setProperty("sql-show", show);
        printer.setProperties(props);
        return printer;
    }

//    @Bean
//    public DataSource dataSource() {
//        return new HikariDataSource();
//    }

//    @Bean
//    public PageInterceptor pageInterceptor() {
//        return new PageInterceptor();
//    }

}
