package com.github.dreamroute.mybatis.pro.sample.springboot.interceptor;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import com.github.dreamroute.pager.starter.api.PageRequest;
import com.github.dreamroute.pager.starter.api.PageResponse;
import com.github.dreamroute.pager.starter.api.Pager;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PagerTest {

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
                .values("Dreamroute")
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

    @Test
    @Disabled
    void selectByPageTest() {
        PageRequest<User> pr = new PageRequest<>();
        pr.setPageNum(1);
        pr.setPageSize(2);
        pr.setParam(User.builder().name("w.dehai").build());

        PageResponse<User> page = Pager.page(pr, userMapper::selectByPage);
        assertEquals(3L, page.getTotalNum());
    }

}
