package com.github.dreamroute.mybatis.pro.sample.springboot;

import com.github.dreamroute.sqlprinter.starter.anno.EnableSQLPrinter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSQLPrinter
@SpringBootApplication
@MapperScan("com.github.dreamroute.mybatis.pro.sample.springboot.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
