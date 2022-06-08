package com.githu.dreamroute.mybatis.pro.base;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

class MiscTest {

    @Test
    void enumSetTest() {
        EnumSet<Gender> genders = EnumSet.allOf(Gender.class);
        System.err.println(genders);
        EnumSet<Gender> female = EnumSet.of(Gender.FEMALE);
        System.err.println(female);
    }

}
