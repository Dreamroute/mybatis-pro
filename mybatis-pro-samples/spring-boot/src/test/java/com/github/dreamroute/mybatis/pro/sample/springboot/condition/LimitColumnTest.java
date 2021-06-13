package com.github.dreamroute.mybatis.pro.sample.springboot.condition;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.Dict;
import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.DictMapper;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper.FindByIdLTDto;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

import static com.github.dreamroute.mybatis.pro.core.util.MyBatisProUtil.FIELDS_ALIAS_CACHE;
import static com.google.common.collect.Lists.newArrayList;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author : w.dehai.2021.04.01
 */
@SpringBootTest
class LimitColumnTest {
    @Resource
    private UserMapper userMapper;
    @Resource
    private DictMapper dictMapper;
    @Resource
    private DataSource dataSource;

    @BeforeEach
    void init() {

        // init smat_user
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
        Insert insert = insertInto("smart_user")
                .columns("name", "password", "phone_no", "version", "addr_info")
                .values("w.dehai", "123456", "1306006", 1L, "成都")
                .values("Jaedong", "123", "1306006", 1L, "北京")
                .values("w.dehai", "123", "1306006", 2L, "美国")
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();

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
    void findByPasswordTest() {
        // 这里对同一个方法测试2次，测试缓存功能
        User u1 = userMapper.findByPassword("123456", "id", "name");
        User u2 = userMapper.findByPassword("123456", "id", "name", "password");
        List<User> u3 = userMapper.findByIdIn(newArrayList(1L));
        // TODO
    }

    @Test
    void findByIdLTETest() {
        List<User> users =  userMapper.findByIdLTE(2L, new String[] {"id", "name", "password", "version", "phoneNo", "addr"});
        assertEquals(2, users.size());
    }

    /**
     * 限制列插件测试多个selectById是否会冲突，因为都存在于BaseMapper之中，
     */
    @Test
    void selectByIdTest() {
        Dict dict = dictMapper.selectById(1L, "id", "value", "cnName");
        assertEquals("有效", dict.getCnName());
        User user = userMapper.selectById(1L, "id", "name");
        assertEquals("w.dehai", user.getName());
        List<User> all = userMapper.selectAll("id");
        assertEquals(3, all.size());
        List<User> users = userMapper.selectByIds(newArrayList(1L, 2L), "id", "phoneNo");
        assertEquals(2, users.size());
    }

    @Test
    void cacheTest() {
        userMapper.findByIdLT(2L);
        Assertions.assertTrue(FIELDS_ALIAS_CACHE.containsKey(FindByIdLTDto.class));
    }

}
