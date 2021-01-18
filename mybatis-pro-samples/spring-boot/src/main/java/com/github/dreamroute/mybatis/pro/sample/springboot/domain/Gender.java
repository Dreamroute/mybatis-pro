package com.github.dreamroute.mybatis.pro.sample.springboot.domain;

import com.github.dreamroute.mybatis.pro.core.typehandler.EnumMarker;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  Gender implements EnumMarker {

    MALE("男", 1), FEMALE("女", 2);

    private final String desc;
    private final Integer value;

}
