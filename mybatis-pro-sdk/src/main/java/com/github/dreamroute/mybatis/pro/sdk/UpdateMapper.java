package com.github.dreamroute.mybatis.pro.sdk;

/**
 * @author w.dehai
 */
public interface UpdateMapper<T> {

    int updateById(T entity);

    int updateByIdExcludeNull(T entity);

}
