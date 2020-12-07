package com.github.dreamroute.mybatis.pro.sample.springboot.config.resp;

/**
 * 
 * @author w.dehai
 *
 */
public enum RespEnum {

    /**
     * 请求成功
     */
    SUCCESS("0", "请求成功"),

    /**
     * 登录异常
     */
    LOGIN_FAILD("10000", "登录异常，用户名或密码错误"),

    /**
     * 系统异常，主要针对500错误，开发者禁止使用此类型
     */
    SYSTEM_ERR("10001", "提交的信息不符合规范,请重新提交"),

    /**
     * 新增失败
     */
    INSERT_ERR("10002", "新增失败"),

    /**
     * 删除失败
     */
    DELETE_ERR("10003", "删除失败"),

    /**
     * 修改失败
     */
    UPDATE_ERR("10004", "修改失败"),
    ;

    RespEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
    
    public RespEnum code(String code) {
        this.code = code;
        return this;
    }

    public RespEnum desc(String desc) {
        this.desc = desc;
        return this;
    }

}
