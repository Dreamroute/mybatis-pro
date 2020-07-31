package com.github.dreamroute.mybatis.pro.sample.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;

@SpringBootTest
public class SdkTest {
    
    @Autowired
    private UserMapper userMapper;
    
    @Test
    void selectByIdTest() {
        User user = userMapper.selectById(1L);
        System.err.println(user);
    }

}
