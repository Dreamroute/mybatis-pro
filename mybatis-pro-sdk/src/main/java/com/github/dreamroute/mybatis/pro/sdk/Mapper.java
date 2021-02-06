package com.github.dreamroute.mybatis.pro.sdk;

/**
 * crud接口集合
 */
public interface Mapper<T, ID> extends
        SelectMapper<T, ID>,
        InsertMapper<T>,
        UpdateMapper<T>,
        DeleteMapper<ID> {}
