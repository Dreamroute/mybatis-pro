package com.github.dreamroute.mybatis.pro.boot.starter;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class NoneTest {

    @Test
    void none() {
        None none = new None();
        assertNotEquals(none, new None());
    }

}
