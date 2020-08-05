package com.github.dreamroute.mybatis.pro.core.consts;

/**
 * mapper.xml关键字
 *
 * @author w.dehai
 */
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

    private String code;

    MapperLabel(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
