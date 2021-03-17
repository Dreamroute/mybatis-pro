package com.github.dreamroute.mybatis.pro.sample.springboot.condition;

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

/**
 * @author w.dehai
 */
@SpringBootTest
class CountByTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
        Insert insert = insertInto("smart_user")
                .columns("id", "name", "password", "phone_no")
                .values(1L, "w.dehai", "123456", "1306006")
                .values(2L, "Jaedong", "123", "1306006")
                .values(3L, "Jaedong", "123", "1352332")
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

    @Test
    void countByNameTest() {
        int result = userMapper.countByName("w.dehai");
        assertEquals(1, result);
    }

    @Test
    void countByNameAndPhoneNo() {
        int result = userMapper.countByNameAndPhoneNo("w.dehai", "1306006");
        assertEquals(1, result);
    }

    @Test
    void countByNameAndPhoneNoCanEmptyTest() {
        assertEquals(2, userMapper.countByNameAndPhoneNoCanEmpty("", "1306006"));
        assertEquals(2, userMapper.countByNameAndPhoneNoCanEmpty(null, "1306006"));
        assertEquals(3, userMapper.countByNameAndPhoneNoCanEmpty("", ""));
        assertEquals(3, userMapper.countByNameAndPhoneNoCanEmpty(null, null));
        assertEquals(3, userMapper.countByNameAndPhoneNoCanEmpty(null, ""));
    }

}
