package com.github.dreamroute.mybatis.pro.core.annotations;

/**
 * id策略，支持自增、自定义，默认自增
 *
 * @author w.dehai
 */
public enum Type {
    /**
     * 自增
     */
    IDENTITY,
    /**
     * 自定义
     */
    AUTO;
}
