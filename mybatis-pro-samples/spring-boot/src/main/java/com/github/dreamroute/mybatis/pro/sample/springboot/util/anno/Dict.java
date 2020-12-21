package com.github.dreamroute.mybatis.pro.sample.springboot.util.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记实体中从字典取值的注解
 *
 * @author w.dehai
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {

    /**
     * 手动指定DictMap的enName
     */
    String value() default "";
}
