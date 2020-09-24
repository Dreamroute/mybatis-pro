package com.github.dreamroute.mybatis.pro.sdk;

import java.util.List;

/**
 * @author w.dehai
 */
public interface DeleteMapper<ID> {

    /**
     * 根据id删除
     */
    int deleteById(ID id);

    /**
     * 根据id批量删除
     */
    int deleteByIds(List<ID> ids);

}
