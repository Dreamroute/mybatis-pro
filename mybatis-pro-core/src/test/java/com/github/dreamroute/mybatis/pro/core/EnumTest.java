package com.github.dreamroute.mybatis.pro.core;

import com.github.dreamroute.mybatis.pro.core.typehandler.EnumMarker;
import com.github.dreamroute.mybatis.pro.core.typehandler.EnumUtil;
import org.junit.jupiter.api.Test;

import static com.github.dreamroute.mybatis.pro.core.typehandler.EnumUtil.valueOf;

public class EnumTest {

    static enum Gender implements EnumMarker {
        MALE("男", 1), FEMALE("女", 2);

        private String desc;
        private Integer value;

        Gender(String desc, Integer value) {
            this.desc = desc;
            this.value = value;
        }

        @Override
        public int getValue() {
            return this.value;
        }
    }

    @Test
    void createEnumTest() {
        Gender male = EnumUtil.valueOf(Gender.class, 1);
        System.err.println(male);
        Gender female = valueOf(Gender.class, 2);
        System.err.println(female);
    }

}
