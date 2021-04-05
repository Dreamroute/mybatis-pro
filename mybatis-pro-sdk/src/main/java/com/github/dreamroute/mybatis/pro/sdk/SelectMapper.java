package com.github.dreamroute.mybatis.pro.sdk;

import java.util.List;

/**
 * @author w.dehai
 */
public interface SelectMapper<T, ID> {

    /**
     * 根据id查询单个
     *
     * @param id 主键id
     * @param cols 需要查询的列名，不传代表查询全部列
     * @return 返回实体对象
     */
    T selectById(ID id, String... cols);

    /**
     * 根据多个id查询多个
     *
     * @param list 主键id列表
     * @param cols 需要查询的列名，不传代表查询全部列
     * @return 返回实体对象集合
     */
    List<T> selectByIds(List<ID> list, String... cols);

    /**
     * 全表查询
     *
     * @param cols 需要查询的列名，不传代表查询全部列
     * @return 返回当前表的所有数据
     */
    List<T> selectAll(String... cols);

}
