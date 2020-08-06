package com.github.dream.mybatis.pro.sdk;

import java.util.List;

/**
 * @author w.dehai
 */
public interface UpdateMapper<T> {

    int updateById(T entity);

}
