package com.github.dreamroute.mybatis.pro.sample.springboot;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class SdkTest {
    
    @Autowired
    private UserMapper userMapper;
    
    @Test
    void selectByIdTest() {
        User user = userMapper.selectById(1L);
        System.err.println(user);
    }

    @Test
    void selectAllTest() {
        List<User> users = userMapper.selectAll();
        System.err.println(users);
    }

    @Test
    void selectByIdsTest() {
        List<User> users = userMapper.selectByIds(Arrays.asList(1L, 100L, 101L));
        System.err.println(users);
    }

    @Test
    public void insertTest() {
        User user = new User();
        user.setName("test");
        user.setPassword("test");
        user.setVersion(1L);
        user.setGender(1);

        userMapper.insert(user);
    }

    @Test
    public void insertListTest() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setName("test");
        user1.setPassword("test");
        user1.setVersion(1L);
        user1.setGender(1);
        users.add(user1);

        User user2 = new User();
        user2.setName("test");
        user2.setPassword("test");
        user2.setVersion(1L);
        user2.setGender(1);
        users.add(user2);

        int result = userMapper.insertList(users);
        System.err.println(result);
    }

    @Test
    void updateByIdTest() {
        User user = userMapper.selectById(2L);
        user.setPhoneNo("13060067253");
        int result = userMapper.updateById(user);
        System.err.println(result);
    }

    @Test
    void deleteByIdTest() {
        userMapper.deleteById(107L);
    }

    @Test
    void deleteByIdsTest() {
        userMapper.deleteByIds(Arrays.asList(105L, 106L));
    }

}
