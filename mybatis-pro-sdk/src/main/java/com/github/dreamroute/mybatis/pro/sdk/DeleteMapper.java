package com.github.dreamroute.mybatis.pro.sdk;

import java.util.List;

/**
 * @author w.dehai
 */
public interface DeleteMapper<ID> {

    int deleteById(ID id);

    int deleteByIds(List<ID> ids);

}
