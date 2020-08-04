package com.github.dreamroute.mybatis.pro.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键注解
 *
 * @author w.dehai
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {

    /**
     * 默认自增
     */
    Type type() default Type.IDENTITY;

}
