package com.github.dreamroute.mybatis.pro.sample.springboot.sdk;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ninja_squad.dbsetup.Operations.truncate;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author w.dehai
 */
@SpringBootTest
class InsertTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
    }

    @Test
    void insertTest() {
        User user = User.builder().name("w.dehai").password("123456").version(1L).phoneNo("1306006").build();
        userMapper.insert(user);
        assertEquals(1, userMapper.selectAll().size());
    }

    @Test
    void insertExcludeNullTest() {
        User user = User.builder().name("w.dehai").version(1L).build();
        userMapper.insertExcludeNull(user);
        List<User> users = userMapper.selectAll();
        assertEquals("123456", users.get(0).getPassword());
    }

    @Test
    void insertListTest() {
        int size = 3;
        List<User> users = new ArrayList<>(3);
//        for (int i = 0; i < size; i++) {
//            User user = User.builder().name("w.dehai").password("123456").version(1L).phoneNo("1306006").build();
//            users.add(user);
//        }
        userMapper.insertList(users);
        List<Long> ids = users.stream().map(User::getId).filter(Objects::nonNull).collect(toList());
        assertEquals(size, ids.size());
    }

}






























