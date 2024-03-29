package com.github.dreamroute.mybatis.pro.core;

import com.github.dreamroute.mybatis.pro.base.codec.enums.EnumMarker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static com.github.dreamroute.mybatis.pro.core.EnumTest.Gender.FEMALE;
import static com.github.dreamroute.mybatis.pro.core.EnumTest.Gender.MALE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnumTest {

    @Getter
    @AllArgsConstructor
    enum Gender implements EnumMarker {

        MALE(1, "男"),
        FEMALE(2, "女");

        private final Integer value;
        private final String desc;

    }

    @Test
    void createEnumTest() {
        Gender male = EnumMarker.valueOf(Gender.class, 1);
        assertEquals(MALE, male);
        Gender female = EnumMarker.valueOf(Gender.class, 2);
        assertEquals(FEMALE, female);

        // 抛异常
        assertThrows(IllegalArgumentException.class, () -> EnumMarker.valueOf(Gender.class, 3));
    }

}
