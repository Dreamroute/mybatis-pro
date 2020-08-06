package com.github.dream.mybatis.pro.sdk;

/**
 * @author w.dehai
 */
public interface UpdateMapper<T> {

    int updateById(T entity);

}
