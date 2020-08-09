package com.github.dreamroute.mybatis.pro.sample.springboot;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * w.dehai
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void findByNameAndPasswordTest() {
        User user = userMapper.findByNameAndPassword("w.dehai", "1234");
        Assertions.assertEquals(1L, user.getId());
    }

    @Test
    void findByNameTest() {
        List<User> users = userMapper.findByName("w.dehai");
        Assertions.assertEquals(1, users.size());
    }

    @Test
    void findByNameAndPasswordLikeTest() {
        List<User> users = userMapper.findByNameAndPasswordLike("w.dehai", "23");
        Assertions.assertEquals(1, users.size());
    }

    @Test
    void findByVersionOrderByIdTest() {
        List<User> users = userMapper.findByVersionOrderByIdDesc(1L);
        if (users != null) {
            users.stream().map(User::getId).forEach(System.err::println);
        }
        Assertions.assertEquals(1, users.size());
    }

    @Test
    void findByNameLikeAndVersionOrPasswordOrderByIdTest() {
        List<User> users = userMapper.findByNameLikeAndVersionOrPasswordOrderById("w.dehai", 1L, "123");
        System.err.println(users);
    }

    @Test
    void findByIdInTest() {
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        List<User> users = userMapper.findByIdIn(list);
        System.err.println(users);
    }

    @Test
    public void findByIdNotIn() {
        List<Long> list = new ArrayList<>();
        list.add(2L);
        List<User> users = userMapper.findByIdNotIn(list);
        System.err.println(users);
    }

    @Test
    void findByNameCountTest() {
        int count = userMapper.countByName("123");
        System.err.println(count);
    }

    @Test
    void findByNameAndPhoneNoTest() {
        boolean count = userMapper.countByNameAndPhoneNo("bai", "13060067253");
        System.err.println(count);
    }

}
