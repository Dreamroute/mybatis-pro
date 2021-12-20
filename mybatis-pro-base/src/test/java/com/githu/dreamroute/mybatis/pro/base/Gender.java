package com.githu.dreamroute.mybatis.pro.base;

import com.github.dreamroute.mybatis.pro.base.enums.EnumMarker;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author w.dehai.2021/8/10.14:49
 */
@Getter
@AllArgsConstructor
public enum Gender implements EnumMarker {
    MALE(1, "男"),
    FEMALE(2, "女");

    private final Integer value;
    private final String desc;
}
