package com.github.dreamroute.mybatis.pro.sample.springboot.condition;

import com.github.dreamroute.common.util.test.Appender;
import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import com.github.dreamroute.sqlprinter.starter.interceptor.SqlPrinter;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author w.dehai
 */
@SpringBootTest
class LimitTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
        Insert insert = insertInto("smart_user")
                .columns("name", "password", "phone_no", "version", "status")
                .values("w.dehai", "123456", "1306006", 1L, 1)
                .values("Jaedong", "123", "1306006", 1L, 1)
                .values("w.dehai", "123", "1306006", 2L, 1)
                .values("w.dehai", "123", "1306006", 2L, 1)
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

    @Test
    void findByNameLimitTest() {
        Appender appender = new Appender(SqlPrinter.class);
        List<User> result = userMapper.findByNameLimit("w.dehai", 2);
        assertEquals(2, result.size());
        assertTrue(appender.contains(
                "SELECT password, order_id AS orderId, name, id, addr_info AS addr\n" +
                        "\t, version, phone_no AS phoneNo, status\n" +
                        "FROM smart_user\n" +
                        "WHERE name = 'w.dehai'\n" +
                        "LIMIT 2")
        );
    }

    @Test
    void findByNameAndPasswordOrderByIdLimitTest() {
        Appender appender = new Appender(SqlPrinter.class);
        List<User> users = userMapper.findByNameAndPasswordOrderByIdLimit("w.dehai", "123", 2);
        assertEquals(2, users.size());
        assertTrue(appender.contains(
                "SELECT password, order_id AS orderId, name, id, addr_info AS addr\n" +
                        "\t, version, phone_no AS phoneNo, status\n" +
                        "FROM smart_user\n" +
                        "WHERE name = 'w.dehai'\n" +
                        "\tAND password = '123'\n" +
                        "ORDER BY id\n" +
                        "LIMIT 2"
        ));
    }
}














