package com.github.dreamroute.mybatis.pro.sample.springboot.config.resp;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 被此注解标注的类或者方法返回值不进行转换
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Keep {}
