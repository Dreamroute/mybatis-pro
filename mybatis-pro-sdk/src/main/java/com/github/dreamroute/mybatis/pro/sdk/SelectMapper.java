package com.github.dreamroute.mybatis.pro.sdk;

import java.util.List;

/**
 * @author w.dehai
 */
public interface SelectMapper<T, ID> {
    
    T selectById(ID id);
    
    List<T> selectByIds(List<ID> ids);

    List<T> selectAll();

}
