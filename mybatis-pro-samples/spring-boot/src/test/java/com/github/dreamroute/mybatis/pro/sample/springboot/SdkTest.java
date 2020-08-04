package com.github.dreamroute.mybatis.pro.sample.springboot;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}
