package com.github.dreamroute.mybatis.pro.core;

import com.github.dreamroute.mybatis.pro.base.typehandler.EnumMarker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static com.github.dreamroute.mybatis.pro.core.EnumTest.Gender.FEMALE;
import static com.github.dreamroute.mybatis.pro.core.EnumTest.Gender.MALE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumTest {

    @Getter
    @AllArgsConstructor
    enum Gender implements EnumMarker {

        MALE(1, "男", 1),
        FEMALE(2, "女", 2);

        private final Integer value;
        private final String desc;
        private final Integer sort;

    }

    @Test
    void createEnumTest() {
        Gender male = EnumMarker.valueOf(Gender.class, 1);
        assertEquals(MALE, male);
        Gender female = EnumMarker.valueOf(Gender.class, 2);
        assertEquals(FEMALE, female);
    }

}
