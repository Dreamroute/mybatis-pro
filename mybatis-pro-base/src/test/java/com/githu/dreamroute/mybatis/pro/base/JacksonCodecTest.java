package com.githu.dreamroute.mybatis.pro.base;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.dreamroute.mybatis.pro.base.enums.EnumMarkerDeserializer;
import com.github.dreamroute.mybatis.pro.base.enums.JsonUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.githu.dreamroute.mybatis.pro.base.Gender.FEMALE;
import static com.githu.dreamroute.mybatis.pro.base.Gender.MALE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author w.dehai.2021/8/10.14:51
 */
class JacksonCodecTest {

    @Test
    void baseTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Enum.class, new EnumMarkerDeserializer());
        mapper.registerModule(module);

        User male = mapper.readValue("{\"gender\":1,\"id\":100}", User.class);
        assertEquals(MALE, male.getGender());
        User female = mapper.readValue("{\"gender\":2,\"id\":100}", User.class);
        assertEquals(FEMALE, female.getGender());

        assertThrows(JsonMappingException.class, () -> mapper.readValue("{\"gender\":28,\"id\":100}", User.class));

    }

    @Test
    void jsonUtilObjTest() {
        User user = new User();
        user.setId(100L);
        user.setGender(FEMALE);

        String str = JsonUtil.toJsonStr(user);
        assertEquals("{\"id\":100,\"gender\":2}", str);

        User u = JsonUtil.parseObj(str, User.class);
        assertEquals(100L, u.getId());
        assertEquals(FEMALE, u.getGender());

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
        assertEquals("[{\"id\":1,\"gender\":1},{\"id\":2,\"gender\":2}]", str);

        List<User> us = JsonUtil.parseArr(str, User.class);
        assertEquals(MALE, us.get(0).getGender());
        assertEquals(FEMALE, us.get(1).getGender());
    }

}
