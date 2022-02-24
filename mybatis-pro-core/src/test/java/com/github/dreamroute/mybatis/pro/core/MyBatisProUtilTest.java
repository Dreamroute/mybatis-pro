package com.github.dreamroute.mybatis.pro.core;

import com.github.dreamroute.mybatis.pro.core.annotations.Column;
import com.github.dreamroute.mybatis.pro.core.annotations.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Map;

import static com.github.dreamroute.mybatis.pro.core.util.MyBatisProUtil.getFieldAliasMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author w.dehai
 */
public class MyBatisProUtilTest {

    @Test
    public void getFieldAliasMapTest() {
        Map<String, String> map = getFieldAliasMap(Demo.class);
        assertTrue(map.containsKey("id"));
        assertTrue(map.containsKey("name"));
        assertTrue(map.containsKey("phoneNo"));
        assertEquals("phone_no", map.get("phoneNo"));
        assertFalse(map.containsKey("serialVersionUID"));
        assertFalse(map.containsKey("tx"));

    }

    @Data
    public static class BaseDomain {
        private Long id;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Demo extends BaseDomain implements Serializable {
        private static final long serialVersionUID = -1L;
        private String name;
        @Column("phone_no")
        private String phoneNo;
        @Transient
        private String tx;
    }

}
