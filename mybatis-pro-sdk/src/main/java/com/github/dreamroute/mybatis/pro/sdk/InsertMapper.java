package com.github.dreamroute.mybatis.pro.sdk;

import java.util.List;

/**
 * @author w.dehai
 */
public interface InsertMapper<T> {

    /**
     * 新增单个
     */
    int insert(T entity);

    /**
     * 单个新增（实体为空的属性不新增到数据，使用数据库默认值）
     */
    int insertExcludeNull(T entity);

    /**
     * 批量新增（实体为空的属性也更新到数据库，目前还做不到insertListExclude）
     */
    int insertList(List<T> entityList);

}
