package com.github.dreamroute.mybatis.pro.sample.springboot.condition;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author : w.dehai.2021.04.01
 */
@SpringBootTest
class LimitColumnTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DataSource dataSource;

    @Value("${phone-no}")
    private String phoneNo;

    @BeforeEach
    void init() {
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
        Insert insert = insertInto("smart_user")
                .columns("name", "password", phoneNo, "version")
                .values("w.dehai", "123456", "1306006", 1L)
                .values("Jaedong", "123", "1306006", 1L)
                .values("w.dehai", "123", "1306006", 2L)
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

    @Test
    void findByPasswordTest() {
        // 这里对同一个方法测试2次，测试缓存功能
        for (int i = 0; i < 2; i++) {
            User user = userMapper.findByPassword("123456", "id", "name");
            assertEquals(1L, user.getId());
        }
    }

    @Test
    void findByIdLTETest() {
        List<User> users =  userMapper.findByIdLTE(2L, new String[] {"id", "name", "password", "version"});
        assertEquals(2, users.size());
    }
}
