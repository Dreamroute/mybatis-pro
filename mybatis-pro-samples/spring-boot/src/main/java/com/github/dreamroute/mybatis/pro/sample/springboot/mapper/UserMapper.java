package com.github.dreamroute.mybatis.pro.sample.springboot.mapper;

import com.github.dream.mybatis.pro.sdk.Mapper;
import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;

import java.util.List;

public interface UserMapper extends Mapper<User, Long> {

    User findByNameAndPassword(String name, String password);

    List<User> findByName(String name);

    List<User> findByNameAndPasswordLike(String name, String password);

    List<User> findByVersionOrderByIdDesc(Long version);

    List<User> findByNameLikeAndVersionOrPasswordOrderById(String name, Long version, String password);

    List<User> findByIdIn(List<Long> list);

    List<User> findByIdNotIn(List<Long> list);

    int countByName(String name);

    boolean countByNameAndPhoneNo(String name, String phoneNo);

    int deleteByNameOrPassword(String name, String password);

    List<User> findByVersionLT(Long version);

    List<User> findByVersionLTE(Long version);

    List<User> findByVersionGT(Long version);

    List<User> findByVersionGTE(Long version);

    List<User> findByNameIsNull();

    List<User> findByNameIsNotNull();

    List<User> findByNameIsBlank();

    List<User> findByNameIsNotBlank();

    List<User> findByNameLike(String name);

    List<User> findByNameNotLike(String name);

    List<User> findByNameStartWith(String name);

    List<User> findByNameEndWith(String name);

    List<User> findByNameNot(String name);

    List<User> findByNameIn(List<String> name);

    List<User> findByNameNotIn(List<String> names);

    List<User> findByNameOrderByVersion(String name);

    List<User> findByNameOrderByVersionDesc(String name);
}
