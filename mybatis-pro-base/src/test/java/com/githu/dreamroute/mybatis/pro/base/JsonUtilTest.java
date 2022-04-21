package com.githu.dreamroute.mybatis.pro.base;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.dreamroute.mybatis.pro.base.enums.EnumMarkerDeserializer;
import com.github.dreamroute.mybatis.pro.base.enums.JsonUtil;
import com.github.dreamroute.mybatis.pro.base.time.DateDeserializer;
import com.github.dreamroute.mybatis.pro.base.time.DateSerializer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.githu.dreamroute.mybatis.pro.base.Gender.FEMALE;
import static com.githu.dreamroute.mybatis.pro.base.Gender.MALE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author w.dehai.2021/8/10.14:51
 */
class JsonUtilTest {

    private static final String BIRTHDAY_STR = "2022-05-05 15:05:12.333";
    private static final Date BIRTHDAY = DateUtil.parse(BIRTHDAY_STR, DateSerializer.FORMAT);

    @Test
    void deserializeTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Enum.class, new EnumMarkerDeserializer());
        module.addDeserializer(Date.class, new DateDeserializer());
        mapper.registerModule(module);

        User male = mapper.readValue("{\n" +
                "  \"id\": 100,\n" +
                "  \"gender\": 1,\n" +
                "  \"birthday\": \"2022-05-05 15:05:12.333\"\n" +
                "}", User.class);

        User female = mapper.readValue("{\n" +
                "  \"id\": 100,\n" +
                "  \"gender\": 2,\n" +
                "  \"birthday\": \"2022-05-05 15:05:12.333\"\n" +
                "}", User.class);

        assertEquals(MALE, male.getGender());
        assertEquals(FEMALE, female.getGender());
        assertEquals(BIRTHDAY, male.getBirthday());

        assertThrows(JsonMappingException.class, () -> mapper.readValue("{\"gender\":28,\"id\":100}", User.class));

    }

    @Test
    void jsonUtilObjTest() {
        User user = new User();
        user.setId(100L);
        user.setBirthday(BIRTHDAY);
        user.setGender(FEMALE);

        String str = JsonUtil.toJsonStr(user);
        assertEquals("{\"id\":100,\"gender\":2,\"birthday\":\"2022-05-05 15:05:12.333\"}", str);

        User u = JsonUtil.parseObj(str, User.class);
        assertEquals(100L, u.getId());
        assertEquals(FEMALE, u.getGender());
        assertEquals(BIRTHDAY, u.getBirthday());

    }

    @Test
    void jsonUtilArrTest() {
        User user1 = new User();
        user1.setId(1L);
        user1.setGender(MALE);

        User user2 = new User();
        user2.setId(2L);
        user2.setGender(FEMALE);

        List<User> users = new ArrayList<>(2);
        users.add(user1);
        users.add(user2);

        String str = JsonUtil.toJsonStr(users);
        assertEquals("[{\"id\":1,\"gender\":1,\"birthday\":null},{\"id\":2,\"gender\":2,\"birthday\":null}]", str);

        List<User> us = JsonUtil.parseArr(str, User.class);
        assertEquals(MALE, us.get(0).getGender());
        assertEquals(FEMALE, us.get(1).getGender());
    }

    @Test
    void serializerForWebTest() throws Exception {

        User user = new User();
        user.setId(100L);
        user.setGender(MALE);
        String male = JsonUtil.toJsonStrForWeb(user);
        assertEquals("{\"id\":100,\"gender\":{\"value\":1,\"desc\":\"男\"},\"birthday\":null}", male);

        user.setGender(FEMALE);
        String female = JsonUtil.toJsonStrForWeb(user);
        assertEquals("{\"id\":100,\"gender\":{\"value\":2,\"desc\":\"女\"},\"birthday\":null}", female);

    }

}
