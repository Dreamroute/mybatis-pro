package com.github.dreamroute.mybatis.pro.sample.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;

@SpringBootApplication
@MapperScan(basePackages = {"com.github.dreamroute.mybatis.pro.sample.springboot.mapper"}, value = "com.github.dreamroute.mybatis.pro.sample.springboot.mapper", basePackageClasses = UserMapper.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
