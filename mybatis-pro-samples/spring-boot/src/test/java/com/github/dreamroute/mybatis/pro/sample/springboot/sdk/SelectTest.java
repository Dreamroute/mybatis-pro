package com.github.dreamroute.mybatis.pro.sample.springboot.sdk;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author w.dehai
 */
@SpringBootTest
class SelectTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
        Insert insert = insertInto("smart_user")
                .columns("name", "status")
                .values("w.dehai", 1)
                .values("Jaedong", 1)
                .values("Dreamroute", 1)
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

    @Test
    void selectByIdTest() {
        User user = userMapper.selectById(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    void selectByIdsTest() {
        List<User> users = userMapper.selectByIds(Arrays.asList(2L, 3L));
        Long[] ids = users.stream().map(User::getId).toArray(Long[]::new);
        assertArrayEquals(new Long[]{2L, 3L}, ids);
    }

    @Test
    void selectAllTest() {
        List<User> users = userMapper.selectAll();
        Long[] ids = users.stream().map(User::getId).toArray(Long[]::new);
        assertArrayEquals(new Long[]{1L, 2L, 3L}, ids);
    }

    @Test
    void selectByNameAndPasswordTest() {
        List<User> users = userMapper.selectByNameAndPasssword("w.dehai", "123456");
        System.err.println(users);
    }

}
