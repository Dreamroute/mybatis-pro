package com.github.dream.mybatis.pro.sdk;

import java.util.List;

/**
 * @author w.dehai
 */
public interface InsertMapper<T> {

    int insert(T entity);

    int insertExcludeNull(T entity);

    int insertList(List<T> entityList);

}
