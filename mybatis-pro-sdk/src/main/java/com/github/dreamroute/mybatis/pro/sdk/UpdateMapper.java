package com.github.dreamroute.mybatis.pro.sdk;

import com.github.dreamroute.locker.anno.Locker;

/**
 * @author w.dehai
 */
public interface UpdateMapper<T> {

    /**
     * 根据id修改（实体为空的属性也更新到数据库）
     */
    @Locker
    int updateById(T entity);

    /**
     * 根据id修改（实体为空的属性不更新到数据库）
     */
    @Locker
    int updateByIdExcludeNull(T entity);

}
