package com.github.dreamroute.mybatis.pro.sample.springboot.service;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
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

import static com.google.common.collect.Lists.newArrayList;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author w.dehai
 */
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
    }

    @Test
    void insertTest() {
        User user = User.builder().name("w.dehai").version(1L).build();
        User result = userService.insert(user);
        assertNotNull(result.getId());

        List<User> users = userService.selectAll();
        assertEquals(1, users.size());
    }

    @Test
    void insertListTest() {
        int size = 3;
        List<User> users = new ArrayList<>(3);
        for (int i = 0; i < size; i++) {
            User user = User.builder().name("w.dehai").password("123456").version(1L).phoneNo("1306006").build();
            users.add(user);
        }
        userService.insertList(users);
        List<Long> ids = users.stream().map(User::getId).filter(Objects::nonNull).collect(toList());
        assertEquals(size, ids.size());
    }

    @Test
    void deleteTest() {
        User user = User.builder().name("w.dehai").version(1L).build();
        User result = userService.insert(user);
        assertNotNull(result.getId());

        userService.delete(user.getId());
    }

    @Test
    void deleteListTest() {
        List<User> users = newArrayList(
                User.builder().name("w.dehai").version(1L).build(),
                User.builder().name("w.dehai").version(2L).build()
        );
        userService.insertList(users);

        int result = userService.delete(newArrayList(1L, 2L));
        assertEquals(2, result);
    }

}
