package com.github.dreamroute.mybatis.pro.service.service;

import java.util.List;

/**
 * @author w.dehai
 */
public interface BaseService<T, ID> {

    /**
     * 单个新增
     */
    T insert(T entity);

    /**
     * 单个新增（实体为空的属性不新增到数据）
     */
    T insertExcludeNull(T entity);

    /**
     * 批量新增（实体为空的属性也更新到数据库）
     */
    List<T> insertList(List<T> entityList);

    /**
     * 根据id删除单个（逻辑删除，被删除的记录会保存在备份表）
     */
    int delete(ID id);

    /**
     * 根据id删除单个（物理删除）
     */
    int deleteDanger(ID id);

    /**
     * 根据id批量删除（逻辑删除，被删除的记录会保存在备份表）
     */
    int delete(List<ID> ids);

    /**
     * 批量删除（物理删除）
     */
    int deleteDanger(List<ID> ids);

    /**
     * 根据id更新（实体属性为空的列也将更新到数据）
     */
    int update(T entity);

    /**
     * 根据id更新（实体属性为空的列不更新到数据）
     */
    int updateExcludeNull(T entity);

    /**
     * 根据id单个查询
     */
    T select(ID id);

    /**
     * 根据id批量查询
     */
    List<T> select(List<ID> ids);

    /**
     * 全表查询
     */
    List<T> selectAll();

}
