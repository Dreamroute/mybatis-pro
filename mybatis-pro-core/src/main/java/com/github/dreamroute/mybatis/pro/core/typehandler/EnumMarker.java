package com.github.dreamroute.mybatis.pro.core.typehandler;

/**
 * 枚举类型标记接口，实现此接口的枚举类型会被mybatis自动进行转型
 *
 * @author w.dehai
 */
public interface EnumMarker {
    int getValue();
}