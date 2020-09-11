package com.github.dreamroute.mybatis.pro.sdk;

/**
 * crud接口集合
 */
public interface BaseMapper<T, ID> extends
        SelectMapper<T, ID>,
        InsertMapper<T>,
        UpdateMapper<T>,
        DeleteMapper<ID> {}
