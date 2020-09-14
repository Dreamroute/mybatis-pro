package com.github.dreamroute.mybatis.pro.core;

import com.github.dreamroute.mybatis.pro.sdk.BaseMapper;

import java.util.List;

/**
 * @author w.dehai
 */
public interface DemoMapper extends BaseMapper<Demo, Long> {

    Demo findByNameAndPassword(String name, String password);

    List<Demo> findByName(String name);

    Demo findById(Long id);

}
