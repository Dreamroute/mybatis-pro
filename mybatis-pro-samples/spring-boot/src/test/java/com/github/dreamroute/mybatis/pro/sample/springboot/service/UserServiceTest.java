package com.github.dreamroute.mybatis.pro.sample.springboot.service;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author w.dehai
 */
@SpringBootTest
public class UserServiceTest {

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
    void deleteTest() {
        User user = User.builder().name("w.dehai").version(1L).build();
        User result = userService.insert(user);
        assertNotNull(result.getId());

        userService.delete(user.getId());
    }

}
