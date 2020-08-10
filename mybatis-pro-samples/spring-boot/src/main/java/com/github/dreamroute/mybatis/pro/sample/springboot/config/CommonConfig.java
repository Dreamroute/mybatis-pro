package com.github.dreamroute.mybatis.pro.sample.springboot.config;

import java.util.Properties;

import com.github.dreamroute.mybatis.pro.core.page.PageInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.dreamroute.sqlprinter.interceptor.SqlPrinter;

import javax.sql.DataSource;

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

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource() {
        return new HikariDataSource();
    }

    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

}
