package com.github.dreamroute.mybatis.pro.sample.springboot.domain;

import com.github.dreamroute.mybatis.pro.base.typehandler.EnumMarker;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  Gender implements EnumMarker {

    MALE(1, "男", 1), FEMALE(2, "女", 2);

    private final Integer value;
    private final String desc;
    private final Integer sort;

}
