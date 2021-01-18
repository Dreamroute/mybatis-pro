package com.github.dreamroute.mybatis.pro.sample.springboot.config;

import com.github.dreamroute.mybatis.pro.core.page.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PageConfig {

    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

}
