package com.github.dreamroute.mybatis.pro.sample.springboot.config;

import com.github.dreamroute.mybatis.pro.service.interceptor.PageInterceptor;
import com.github.dreamroute.mybatis.pro.service.interceptor.PagerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PageConfig {

//    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

    @Bean
    public PagerInterceptor pagerInterceptor() {
        return new PagerInterceptor();
    }

}
