package com.github.dreamroute.mybatis.pro.sample.springboot.misc;

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

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 描述：杂项测试
 *
 * @author w.dehi.2022-02-09
 */
@SpringBootTest
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
        Insert insert = insertInto("smart_user")
                .columns("name")
                .values("w.dehai")
                .values("Jaedong")
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

    @Test
    void dynamicSqlSourceTest() {
        List<User> users = userMapper.dynamicSqlSourceTest(newArrayList(1L, 2L));
        assertEquals(2, users.size());
    }

    @Test
    void rawSqlSourceTest() {
        User user = userMapper.rawSqlSourceTest(1L);
        assertEquals("w.dehai", user.getName());
    }
}
