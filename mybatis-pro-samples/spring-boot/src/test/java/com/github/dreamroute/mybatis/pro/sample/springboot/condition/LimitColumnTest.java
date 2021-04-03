package com.github.dreamroute.mybatis.pro.sample.springboot.condition;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.Dict;
import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.DictMapper;
import com.github.dreamroute.mybatis.pro.sample.springboot.mapper.UserMapper;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

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

    @Value("${phone-no}")
    private String phoneNo;
    @Value("${cn-name}")
    private String cnName;

    @BeforeEach
    void init() {

        // init smat_user
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
        Insert insert = insertInto("smart_user")
                .columns("name", "password", phoneNo, "version")
                .values("w.dehai", "123456", "1306006", 1L)
                .values("Jaedong", "123", "1306006", 1L)
                .values("w.dehai", "123", "1306006", 2L)
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();

        // init smat_dict
        new DbSetup(new DataSourceDestination(dataSource), truncate("smart_dict")).launch();
        Insert insert2 = insertInto("smart_dict")
                .columns("value", cnName)
                .values(1, "有效")
                .values(0, "无效")
                .build();
        new DbSetup(new DataSourceDestination(dataSource), insert2).launch();
    }

    @Test
    void findByPasswordTest() {
        // 这里对同一个方法测试2次，测试缓存功能
        User u1 = userMapper.findByPassword("123456", "id", "name");
        List<User> u3 = userMapper.findByIdIn(newArrayList(1L));
        User u2 = userMapper.findByPassword("123456", "id", "name", "password");
        // TODO
    }

    @Test
    void findByIdLTETest() {
        List<User> users =  userMapper.findByIdLTE(2L, new String[] {"id", "name", "password", "version"});
        assertEquals(2, users.size());
    }

    /**
     * 限制列插件测试多个selectById是否会冲突，因为都存在于BaseMapper之中，
     */
    @Test
    void selectByIdTest() {
        User user = userMapper.selectById(1L, "id", "name");
        assertEquals("w.dehai", user.getName());
        Dict dict = dictMapper.selectById(1L, "id", "value", "cnName");
        assertEquals("有效", dict.getCnName());
        List<User> all = userMapper.selectAll("id");
        assertEquals(3, all.size());
        List<User> users = userMapper.selectByIds(newArrayList(1L, 2L), "id", "phoneNo");
        assertEquals(2, users.size());
    }

}
