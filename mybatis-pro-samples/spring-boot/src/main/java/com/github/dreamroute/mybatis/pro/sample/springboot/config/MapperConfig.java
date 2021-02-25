package com.github.dreamroute.mybatis.pro.sample.springboot.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 用于测试多个@MapperScan，没有别的用途
 */
@Configuration
@MapperScan("xxx")
public class MapperConfig {
}
