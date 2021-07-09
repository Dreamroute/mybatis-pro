package com.github.dreamroute.mybatis.pro.sample.springboot.condition;

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

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author w.dehai
 */
@SpringBootTest
class DeleteByTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
        Insert insert = insertInto("smart_user")
                .columns("name", "password")
                .values("w.dehai", "123456")
                .values("Jaedong", "123")
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

    @Test
    void deleteByNameOrPasswordTest() {
        int result = userMapper.deleteByNameOrPassword("w.dehai", "123");
        assertEquals(2, result);
    }

    @Test
    void deleteByNameOrPasswordOptTest() {
        int result = userMapper.deleteByNameOrPasswordOpt("w.dehai", null);
        assertEquals(1, result);
    }

}
