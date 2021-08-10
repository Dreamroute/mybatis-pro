package com.githu.dreamroute.mybatis.pro.base;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.dreamroute.mybatis.pro.base.EnumCodec;
import com.github.dreamroute.mybatis.pro.base.EnumMarker;
import org.junit.jupiter.api.Test;

import static com.githu.dreamroute.mybatis.pro.base.Gender.FEMALE;
import static com.githu.dreamroute.mybatis.pro.base.Gender.MALE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author w.dehai.2021/8/10.14:51
 */
class EnumCodecTest {

    @Test
    void baseTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(EnumMarker.class, new EnumCodec());
        mapper.registerModule(module);

        User male = mapper.readValue("{\"gender\":1,\"id\":100}", User.class);
        assertEquals(MALE, male.getGender());
        User female = mapper.readValue("{\"gender\":2,\"id\":100}", User.class);
        assertEquals(FEMALE, female.getGender());

        assertThrows(JsonMappingException.class, () -> mapper.readValue("{\"gender\":28,\"id\":100}", User.class));

    }

}
