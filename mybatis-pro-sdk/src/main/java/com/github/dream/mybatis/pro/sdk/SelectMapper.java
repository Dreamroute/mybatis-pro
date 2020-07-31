package com.github.dream.mybatis.pro.sdk;

import java.util.List;

/**
 * @author w.dehai
 */
public interface SelectMapper<T, I> {
    
    T selectById(I id);
    
    List<T> selectByIds(List<I> ids);

}
