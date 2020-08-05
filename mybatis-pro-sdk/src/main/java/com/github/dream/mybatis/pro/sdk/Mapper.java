package com.github.dream.mybatis.pro.sdk;

/**
 * crud接口集合
 */
public interface Mapper<T, ID> extends
        SelectMapper<T, ID>,
        InsertMapper<T> {}
