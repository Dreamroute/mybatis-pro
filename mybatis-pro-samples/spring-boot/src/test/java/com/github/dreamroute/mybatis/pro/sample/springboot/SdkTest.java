package com.github.dreamroute.mybatis.pro.sample.springboot;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import com.mysql.cj.util.DnsSrv.SrvRecord;
import org.junit.jupiter.api.Assertions;
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
        Assertions.assertEquals("w.dehai", user.getName());
    }

    @Test
    void selectAllTest() {
        List<User> users = userMapper.selectAll();
        Assertions.assertEquals(2, users.size());
    }

    @Test
    void selectByIdsTest() {
        List<User> users = userMapper.selectByIds(Arrays.asList(1L, 2L));
        Assertions.assertEquals(2, users.size());
    }

    @Test
    public void insertTest() {
        User user = new User();
        user.setName("test");
        user.setPassword("test");
        user.setVersion(1L);
        user.setGender(1);
        user.setPhoneNo("13060067253");

        int result = userMapper.insert(user);
        System.err.println(result);
    }

    @Test
    public void insertListTest() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setName("test");
        user1.setPassword("test");
        user1.setVersion(1L);
        user1.setGender(1);
        user1.setPhoneNo("139");
        users.add(user1);

        User user2 = new User();
        user2.setName("test");
        user2.setPassword("test");
        user2.setVersion(1L);
        user2.setGender(1);
        user2.setPhoneNo("135");
        users.add(user2);

        int result = userMapper.insertList(users);
        System.err.println(result);
    }

    @Test
    void insertExcludeNullTest() {
        User user = new User();
        user.setName("name");
        user.setPassword("pwd");
        user.setGender(1);
        user.setPhoneNo("13060067253");

        int result = userMapper.insertExcludeNull(user);
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
    void updateByIdExcludeNullTest() {
        User user = userMapper.selectById(1L);
        user.setName("bai");
        user.setPhoneNo(null);
        int result = userMapper.updateByIdExcludeNull(user);
        System.err.println(result);
    }

    @Test
    void deleteByIdTest() {
        userMapper.deleteById(1L);
    }

    @Test
    void deleteByIdsTest() {
        userMapper.deleteByIds(Arrays.asList(2L, 4L));
    }

    @Test
    void deleteTest() {
        int result = userMapper.deleteByNameOrPassword("wangdehai", "1234567");
        System.err.println(result);
    }

    @Test
    void lessThanTest() {
        List<User> users = userMapper.findByVersionLT(6L);
        System.err.println(users);
    }

    @Test
    void lessThanEqualTest() {
        List<User> users = userMapper.findByVersionLTE(6L);
        System.err.println(users);
    }

    @Test
    void greaterThanTest() {
        List<User> users = userMapper.findByVersionGT(6L);
        System.err.println(users);
    }

    @Test
    void greaterThanEqualTest() {
        List<User> users = userMapper.findByVersionGTE(6L);
        System.err.println(users);
    }

    @Test
    void isNullTest() {
        List<User> users = userMapper.findByNameIsNull();
        System.err.println(users);
    }

    @Test
    void isNotNullTest() {
        List<User> users = userMapper.findByNameIsNotNull();
        System.err.println(users);
    }

    @Test
    void isBlankTest() {
        List<User> users = userMapper.findByNameIsBlank();
        System.err.println(users);
    }

    @Test
    void isNotBlankTest() {
        List<User> users = userMapper.findByNameIsNotBlank();
        System.err.println(users);
    }

    @Test
    void likeTest() {
        List<User> users = userMapper.findByNameLike("w.de");
        System.err.println(users);
    }

    @Test
    void notLikeTest() {
        List<User> users = userMapper.findByNameNotLike("w.dehai");
        System.err.println(users);
    }

    @Test
    void startWithTest() {
        List<User> users = userMapper.findByNameStartWith("w.d");
        System.err.println(users);
    }

    @Test
    void endWithTest() {
        List<User> users = userMapper.findByNameEndWith("hai");
        System.err.println(users);
    }

    @Test
    void notTest() {
        List<User> users = userMapper.findByNameNot("w.dehai");
        System.err.println(users);
    }

    @Test
    void inTest() {
        List<User> users = userMapper.findByNameIn(Arrays.asList("1", "2", ""));
        System.err.println(users);
    }

    @Test
    void notInTest() {
        List<User> users = userMapper.findByNameNotIn(Arrays.asList("1", "2"));
        System.err.println(users);
    }

    @Test
    void orderByTest() {
        List<User> users = userMapper.findByNameOrderByVersion("w.dehai");
        System.err.println(users);
    }

    @Test
    void descTest() {
        List<User> users = userMapper.findByNameOrderByVersionDesc("w.dehai");
        System.err.println(users);
    }

}
