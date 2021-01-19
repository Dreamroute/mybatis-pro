package com.github.dreamroute.mybatis.pro.core.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * mapper.xml关键字
 *
 * @author w.dehai
 */
@Getter
@AllArgsConstructor
public enum  MapperLabel {

    INSERT("insert"),
    DELETE("delete"),
    UPDATE("update"),
    SELECT("select"),

    MAPPER("mapper"),
    NAMESPACE("namespace"),
    ID("id"),
    RESULT_TYPE("resultType"),
    USE_GENERATED_KEYS("useGeneratedKeys"),
    KEY_PROPERTY("keyProperty");

    private final String code;

}
