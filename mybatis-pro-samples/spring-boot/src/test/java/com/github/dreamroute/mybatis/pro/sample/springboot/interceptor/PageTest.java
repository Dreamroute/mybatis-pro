package com.github.dreamroute.mybatis.pro.sample.springboot.interceptor;

import com.github.dreamroute.mybatis.pro.core.page.PageParam;
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

@SpringBootTest
class PageTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
        Insert insert = insertInto("smart_user")
                .columns("id", "name")
                .values(1L, "w.dehai")
                .values(2L, "Jaedong")
                .values(3L, "Dreamroute")
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

    @Test
    void selectTest() {
        PageParam pageParam = new PageParam();
        Object result = userMapper.selectAllPage(pageParam);
    }

}
