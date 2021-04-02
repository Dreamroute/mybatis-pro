package com.github.dreamroute.mybatis.pro.sdk;

import java.util.List;

/**
 * @author w.dehai
 */
public interface SelectMapper<T, ID> {

    /**
     * 根据id查询单个
     */
    T selectById(ID id, String... cols);

    /**
     * 根据多个id查询多个
     */
    List<T> selectByIds(List<ID> ids, String... cols);

    /**
     * 全表查询
     */
    List<T> selectAll(String... cols);

}
