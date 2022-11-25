package com.github.dreamroute.mybatis.pro.service.service;

import java.util.List;

/**
 * @author w.dehai
 */
public interface BaseService<T, ID> {

    /**
     * 单个新增（<code>null</code>值也会被保存）
     *
     * @param entity 需要保存的实体对象
     * @return 返回保存成功的实体（自增主键会包含在返回值中）
     */
    T insert(T entity);

    /**
     * 单个新增（<code>null</code>值会被忽略）
     *
     * @param entity 需要保存的实体对象
     * @return 返回保存成功的实体（自增主键会包含在返回值中）
     */
    T insertExcludeNull(T entity);

    /**
     * 批量新增（<code>null</code>值会被忽略）
     *
     * @param entityList 需要保存的实体对象列表
     * @return 返回保存成功的实体（自增主键会包含在返回值中）
     */
    List<T> insertList(List<T> entityList);


    /**
     * 批量新增（<code>null</code>值会被忽略）
     *
     * @param entityList 需要保存的实体对象列表
     * @param partition entityList会按照没批partition条数，分批次保存，避免数据太大报错
     * @return 返回保存成功的实体（自增主键会包含在返回值中）
     */
    List<T> insertList(List<T> entityList, int partition);

    /**
     * 根据id删除单个
     */
    int delete(ID id);

    /**
     * 根据id批量删除
     */
    int delete(List<ID> ids);

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
     *
     * @param id 主键
     * @param cols 需要返回的列名数组
     * @return 返回id对应的数据
     */
    T selectById(ID id, String... cols);

    /**
     * 根据id批量查询
     *
     * @param ids 主键列表
     * @param cols 需要返回的列名数组
     * @return 返回id对应的数据
     */
    List<T> selectByIds(List<ID> ids, String... cols);

    /**
     * 全表查询
     *
     * @param cols 需要返回的列名数组
     * @return 返回全部数据
     */
    List<T> selectAll(String... cols);

}
