package com.github.dreamroute.mybatis.pro.sample.springboot.domain;

import com.github.dreamroute.mybatis.pro.core.typehandler.EnumMarker;

public enum  Gender implements EnumMarker {
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
