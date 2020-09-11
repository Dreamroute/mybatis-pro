package com.github.dreamroute.mybatis.pro.service;

import java.util.List;

/**
 * @author w.dehai
 */
public interface BaseService<T, ID> {

    T insert(T entity);

    T insertExcludeNull(T entity);

    List<T> insertList(List<T> entityList);

    int delete(ID id);

    int deleteDanger(ID id);

    int delete(List<ID> ids);

    int deleteDanger(List<ID> ids);

    int update(T entity);

    int updateExcludeNull(T entity);

    T select(ID id);

    List<T> select(List<ID> ids);

    List<T> selectAll();

}
