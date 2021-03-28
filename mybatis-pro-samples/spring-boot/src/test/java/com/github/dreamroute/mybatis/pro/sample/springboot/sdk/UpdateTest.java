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

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author w.dehai
 */
@SpringBootTest
class UpdateTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
        Insert insert = insertInto("smart_user")
                .columns("name")
                .values("w.dehai").build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

    @Test
    void updateByIdTest() {
        User user = userMapper.selectById(1L);
        user.setName("Jaedong");
        user.setVersion(1L);
        user.setPassword(null);
        userMapper.updateById(user);

        User result = userMapper.selectById(1L);
        assertEquals("Jaedong", result.getName());
        assertNull(result.getPassword());
    }

    @Test
    void updateByIdExcludeNull() {
        User user = userMapper.selectById(1L);
        user.setName("Jaedong");
        user.setVersion(1L);
        user.setPassword(null);
        userMapper.updateByIdExcludeNull(user);

        User result = userMapper.selectById(1L);
        assertEquals("Jaedong", result.getName());
        assertEquals("123456", result.getPassword());
    }

}
