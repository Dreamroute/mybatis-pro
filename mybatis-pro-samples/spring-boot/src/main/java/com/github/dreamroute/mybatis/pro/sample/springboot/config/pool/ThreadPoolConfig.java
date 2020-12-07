package com.github.dreamroute.mybatis.pro.sample.springboot.config.pool;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池，异步方法@Async会调用
 * 
 * @author w.dehai
 *
 */
@EnableAsync
@Configuration
public class ThreadPoolConfig implements AsyncConfigurer {
    
    @Override
    public Executor getAsyncExecutor() {
        return new ThreadPoolExecutor(10, 10, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), r -> new Thread(r, "自建线程池的线程"));
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}