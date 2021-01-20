package com.github.dreamroute.mybatis.pro.sample.springboot.interceptor;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import com.github.dreamroute.mybatis.pro.service.adaptor.page.PageRequest;
import com.github.dreamroute.mybatis.pro.service.adaptor.page.PageResponse;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
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
        User user = new User();
        user.setName("w.dehai");
        PageRequest<User> pageRequest = new PageRequest<>();
        pageRequest.setParam(user);

        Page<User> page = PageMethod.startPage(1, 10).doSelectPage(() -> userMapper.selectAllPage(pageRequest.getParam()));
        PageResponse<User> response = new PageResponse<>(page);
        assertEquals(1, response.getData().size());

    }

}
