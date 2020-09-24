package com.github.dreamroute.mybatis.pro.sdk;

/**
 * @author w.dehai
 */
public interface UpdateMapper<T> {

    /**
     * 根据id修改（实体为空的属性也更新到数据库）
     */
    int updateById(T entity);

    /**
     * 根据id修改（实体为空的属性不更新到数据库）
     */
    int updateByIdExcludeNull(T entity);

}
