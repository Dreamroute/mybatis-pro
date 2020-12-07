package com.github.dreamroute.mybatis.pro.sample.springboot.mapper;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.service.mapper.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends Mapper<User, Long> {

    List<User> findByName(String name);

    User findByNameAndPassword(String name, String password);

    List<User> findByNameAndPasswordLike(String name, String password);

    List<User> findByVersionOrderByIdDesc(Long version);

    List<User> findByNameLikeAndVersionOrPasswordOrderById(String name, Long version, String password);

    List<User> findByIdIn(List<Long> list);

    List<User> findByIdNotIn(List<Long> list);

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

    int countByName(String name);

    int countByNameAndPhoneNo(String name, String phoneNo);

    int deleteByNameOrPassword(String name, String password);

    boolean existByNameOrPassword(String name, String password);

    @Select("select * from smart_user where name = #{name} and password = #{password}")
    List<User> selectByNameAndPasssword(String name, String password);
}
