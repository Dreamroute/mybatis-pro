package com.github.dreamroute.mybatis.pro.sample.springboot;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootTest
@MapperScan("com.github.dreamroute.spring.data.mybatis.mapper")
public class UserMapperTest {
    

    @Autowired
    private UserMapper userMapper;

    @Test
    public void findByNameAndPasswordTest() {
        User user = userMapper.findByNameAndPassword("w.dehai", "123");
        System.err.println(user);
    }

    @Test
    void findByNameTest() {
        List<User> users = userMapper.findByName("w.dehai");
        System.err.println(users);
    }

    @Test
    void findByNameAndPasswordLikeTest() {
        List<User> users = userMapper.findByNameAndPasswordLike("w.dehai", "23");
        System.err.println(users);
    }

    @Test
    void findByVersionOrderByIdTest() {
        List<User> users = userMapper.findByVersionOrderByIdDesc(1L);
        if (users != null) {
            users.stream().map(User::getId).forEach(System.err::println);
        }
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
        list.add(1L);
        list.add(2L);
        List<User> users = userMapper.findByIdNotIn(list);
        System.err.println(users);
    }

    @Test
    void mm() {
        Example e = new Example(User.class);
        e.createCriteria()
                .andLike("name", "w.dehai");
        List<User> userList = userMapper.selectByExample(e);
        System.err.println(userList);
    }

}
