package com.github.dreamroute.mybatis.pro.core;

import com.github.dreamroute.mybatis.pro.core.typehandler.EnumMarker;
import org.junit.jupiter.api.Test;

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
        public Integer getValue() {
            return this.value;
        }

        @Override
        public String getDesc() {
            return this.desc;
        }
    }

    @Test
    void createEnumTest() {
        Gender male = EnumMarker.valueOf(Gender.class, 1);
        System.err.println(male);
        Gender female = EnumMarker.valueOf(Gender.class, 2);
        System.err.println(female);
    }

}
