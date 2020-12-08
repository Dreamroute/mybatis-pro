package com.github.dreamroute.mybatis.pro.sample.springboot.config.resp;

/**
 * @author w.dehai
 */
public enum RespEnum {

    SUCCESS(0, "success"),

    LOGIN_FAILD(10000, "登录异常，用户名或密码错误"),

    /**
     * 系统异常，主要针对500错误，让前端看不到500错误
     */
    SYSTEM_ERR(10001, "提交的信息不符合规范，请重新提交"),
    ;

    RespEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public RespEnum code(Integer code) {
        this.code = code;
        return this;
    }

    public RespEnum desc(String desc) {
        this.desc = desc;
        return this;
    }

}
