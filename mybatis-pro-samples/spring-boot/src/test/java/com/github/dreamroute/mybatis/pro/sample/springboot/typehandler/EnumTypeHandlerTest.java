package com.github.dreamroute.mybatis.pro.sample.springboot.typehandler;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.EnumTypeHandler;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.EnumTypeHandlerMapper;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.List;

import static com.github.dreamroute.mybatis.pro.sample.springboot.domain.Gender.MALE;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EnumTypeHandlerTest {

    @Autowired
    private EnumTypeHandlerMapper enumTypeHandlerMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_typehandler")).launch();
    }

    @Test
    void insertTest() {
        EnumTypeHandler enumTypeHandler = new EnumTypeHandler();
        enumTypeHandler.setGender(MALE);
        int result = enumTypeHandlerMapper.insert(enumTypeHandler);
        assertEquals(1, result);
    }

    @Test
    void selectTest() {
        EnumTypeHandler enumTypeHandler = new EnumTypeHandler();
        enumTypeHandler.setGender(MALE);
        enumTypeHandlerMapper.insert(enumTypeHandler);
        List<EnumTypeHandler> all = enumTypeHandlerMapper.selectAll();
        assertEquals(1, all.size());
    }

}

