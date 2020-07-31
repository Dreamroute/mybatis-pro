package com.github.dreamroute.mybatis.pro.sample.springboot.mapper;

import java.util.List;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;

import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User> {

    User findByNameAndPassword(String name, String password);

    List<User> findByName(String name);

    List<User> findByNameAndPasswordLike(String name, String password);

    List<User> findByVersionOrderByIdDesc(Long version);

    List<User> findByNameLikeAndVersionOrPasswordOrderById(String name, Long version, String password);

    List<User> findByIdIn(List<Long> list);

    List<User> findByIdNotIn(List<Long> list);

}
