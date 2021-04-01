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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author w.dehai
 */
@SpringBootTest
class FindByTest {

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
    void findByNameTest() {
        List<User> result = userMapper.findByName("w.dehai");
        assertEquals(2, result.size());
    }

    @Test
    void findByNameAndPasswordTest() {
        User user = userMapper.findByNameAndPassword("Jaedong", "123");
        assertNotNull(user);
    }

    @Test
    void findByNameAndPasswordLikeTest() {
        List<User> users = userMapper.findByNameAndPasswordLike("w.dehai", "345");
        assertEquals(1, users.size());
    }

    @Test
    void findByVersionOrderByIdDescTest() {
        List<User> users = userMapper.findByVersionOrderByIdDesc(1L);
        Long[] ids = users.stream().map(User::getId).toArray(Long[]::new);
        assertArrayEquals(new Long[] {2L, 1L}, ids);
    }

    @Test
    void findByNameLikeAndVersionOrPasswordOrderByIdTest() {
        List<User> users = userMapper.findByNameLikeAndVersionOrPasswordOrderById("dehai", 1L, "123456");
        assertEquals(1, users.size());
    }

    @Test
    void findByIdInTest() {
        List<User> users = userMapper.findByIdIn(Arrays.asList(1L, 2L));
        assertEquals(2, users.size());
    }

    @Test
    void findByIdNotInTest() {
        List<User> users = userMapper.findByIdNotIn(Collections.singletonList(1L));
        assertEquals(2, users.size());
    }

    @Test
    void findByVersionLTTest() {
        List<User> users = userMapper.findByVersionLT(2L);
        assertEquals(2, users.size());
    }

    @Test
    void findByVersionLTETest() {
        List<User> users = userMapper.findByVersionLTE(1L);
        assertEquals(2, users.size());
    }

    @Test
    void findByVersionGTTest() {
        List<User> users = userMapper.findByVersionGT(1L);
        assertEquals(1, users.size());
    }

    @Test
    void findByVersionGTETest() {
        List<User> users = userMapper.findByVersionGTE(1L);
        assertEquals(3, users.size());
    }

    @Test
    void findByNameIsNullTest() {
        List<User> users = userMapper.findByNameIsNull();
        assertTrue(users.isEmpty());
    }

    @Test
    void findByNameIsNotNullTest() {
        List<User> users = userMapper.findByNameIsNotNull();
        assertFalse(users.isEmpty());
    }

    @Test
    void findByNameIsBlankTest() {
        List<User> users = userMapper.findByNameIsBlank();
        assertTrue(users.isEmpty());
    }

    @Test
    void findByNameIsNotBlankTest() {
        List<User> users = userMapper.findByNameIsNotBlank();
        assertEquals(3, users.size());
    }

    @Test
    void findByNameLikeTest() {
        List<User> users = userMapper.findByNameLike("dong");
        assertEquals(1, users.size());
    }

    @Test
    void findByNameNotLikeTest() {
        List<User> users = userMapper.findByNameNotLike("dehai");
        assertEquals("Jaedong", users.get(0).getName());
    }

    @Test
    void findByNameStartWithTest() {
        List<User> users = userMapper.findByNameStartWith("w.d");
        assertEquals(2, users.size());
    }

    @Test
    void findByNameEndWithTest() {
        List<User> users = userMapper.findByNameEndWith("dong");
        assertEquals(1, users.size());
    }

    @Test
    void findByNameNotTest() {
        List<User> users = userMapper.findByNameNot("w.dehai");
        assertEquals("Jaedong", users.get(0).getName());
    }

    @Test
    void findByNameInTest() {
        List<User> users = userMapper.findByNameIn(Arrays.asList("w.dehai", "Jaedong"));
        assertEquals(3, users.size());
    }

    @Test
    void findByNameNotInTest() {
        List<User> users = userMapper.findByNameNotIn(Collections.singletonList("w.dehai"));
        assertEquals("Jaedong", users.get(0).getName());
    }

    @Test
    void findByNameOrderByVersionTest() {
        List<User> users = userMapper.findByNameOrderByVersion("w.dehai");
        Long[] versions = users.stream().map(User::getVersion).toArray(Long[]::new);
        assertArrayEquals(new Long[] {1L, 2L}, versions);
    }

    @Test
    void findByNameOrderByVersionDescTest() {
        List<User> users = userMapper.findByNameOrderByVersionDesc("w.dehai");
        Long[] versions = users.stream().map(User::getVersion).toArray(Long[]::new);
        assertArrayEquals(new Long[] {2L, 1L}, versions);
    }

    @Test
    void findByNameAndPasswordExcludeNullTest() {
        userMapper.findByNameAndPasswordOpt("w.dehai", null);
        userMapper.findByNameAndPasswordOpt(null, "123456");
        userMapper.findByNameAndPasswordOpt(null, null);
        userMapper.findByNameAndPasswordOpt("w.dehai", "");
    }

    @Test
    void findByNameOrderByVersionDescOptTest() {
        userMapper.findByNameOrderByVersionDescOpt(null);
        userMapper.findByNameOrderByVersionDescOpt("");
        userMapper.findByNameOrderByVersionDescOpt("w.dehai");
    }

    @Test
    void findByVersionTest() {
        List<User> users = userMapper.findByVersionOpt(null);
        assertEquals(3, users.size());
    }

}














