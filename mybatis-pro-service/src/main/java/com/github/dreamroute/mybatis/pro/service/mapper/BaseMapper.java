package com.github.dreamroute.mybatis.pro.service.mapper;

import com.github.dreamroute.mybatis.pro.sdk.Mapper;

/**
 * 基础mapper，包含了基础的CRUD方法，原则上业务mapper都应该实现此mapper
 *
 * @author w.dehai
 */
public interface BaseMapper<T, ID> extends Mapper<T, ID> {}
