package com.github.dreamroute.mybatis.pro.core;

import java.util.List;

/**
 * @author w.dehai
 */
public interface DemoMapper {

    Demo findByNameAndPassword(String name, String password);

    List<Demo> findByName(String name);

    Demo findById(Long id);

}
