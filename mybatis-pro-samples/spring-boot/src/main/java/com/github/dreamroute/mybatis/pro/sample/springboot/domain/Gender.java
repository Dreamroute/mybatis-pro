package com.github.dreamroute.mybatis.pro.sample.springboot.domain;

import com.github.dreamroute.mybatis.pro.base.codec.enums.EnumMarker;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  Gender implements EnumMarker {

    MALE(1, "男"), FEMALE(2, "女");

    private final Integer value;
    private final String desc;

}
