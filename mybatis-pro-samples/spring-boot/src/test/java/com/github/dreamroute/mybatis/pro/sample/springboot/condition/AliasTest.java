package com.github.dreamroute.mybatis.pro.sample.springboot.condition;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.Dict;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.DictMapper;
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
class AliasTest {

    @Autowired
    private DictMapper dictMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        // init smat_dict
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_dict")).launch();
        Insert insert2 = insertInto("smart_dict")
                .columns("value", "cn_name")
                .values(1, "有效")
                .values(0, "无效")
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert2).launch();
    }

    @Test
    void aliasTest() {
        Dict result = dictMapper.alias();
        assertEquals("有效", result.getCnName());
    }

}
