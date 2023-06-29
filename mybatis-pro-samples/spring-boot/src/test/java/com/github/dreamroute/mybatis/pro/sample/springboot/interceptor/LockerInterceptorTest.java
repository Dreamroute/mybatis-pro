package com.github.dreamroute.mybatis.pro.sample.springboot.interceptor;

import com.github.dreamroute.locker.anno.EnableLocker;
import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 描述：乐观锁插件测试，测试时在启动类上添加@EnableLocker，观察SQL是否是带有乐观锁
 *
 * @author w.dehi.2022-02-17
 */
@EnableLocker
@SpringBootTest
class LockerInterceptorTest {
    @Resource
    private UserMapper userMapper;
    @Resource
    private DataSource dataSource;

    @BeforeEach
    void init() {
        // init smat_user
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
        Insert insert = insertInto("smart_user")
                .columns("name", "password", "phone_no", "version", "addr_info", "status")
                .values("w.dehai", "123456", "1306006", 1L, "成都", 1)
                .values("Jaedong", "123", "1306006", 1L, "北京", 1)
                .values("w.dehai", "123", "1306006", 2L, "美国", 1)
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

    @Test
    void updateTest() {
        User user = userMapper.selectById(1L);
        user.setPassword("dd");
        int update = userMapper.updateById(user);
        assertEquals(1, update);
        User result = userMapper.selectById(1L);
        assertEquals("dd", result.getPassword());
    }

    @Test
    void updateExcludeNullTest() {
        User user = userMapper.selectById(1L);
        user.setPassword("dd");
        int update = userMapper.updateByIdExcludeNull(user);
        assertEquals(1, update);
        User result = userMapper.selectById(1L);
        assertEquals("dd", result.getPassword());
    }

}
