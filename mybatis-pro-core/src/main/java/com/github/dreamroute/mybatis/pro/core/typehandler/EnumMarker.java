package com.github.dreamroute.mybatis.pro.core.typehandler;

import com.github.dreamroute.mybatis.pro.core.exception.MyBatisProException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 枚举类型标记接口，实现此接口的枚举类型会被mybatis自动进行转型
 *
 * @author w.dehai
 */
public interface EnumMarker {

    /**
     * 返回枚举的value字段的值
     */
    Integer getValue();

    /**
     * 返回枚举的描述信息
     */
    String getDesc();

    /**
     * 根据value字段的值返回对应的枚举类型
     *
     * @param enumCls 枚举类型
     * @param value Integer类型的值
     */
    static  <E extends Enum<?> & EnumMarker> E valueOf(Class<E> enumCls, int value) {
        E[] enumConstants = enumCls.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getValue() == value)
                return e;
        }
        String range = null;
        try {
            range = Arrays.stream(enumConstants).map(EnumMarker::getValue).map(String::valueOf).collect(Collectors.joining(", "));
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new MyBatisProException("您传入的枚举值[" + value + "]不在" + enumCls.getSimpleName() + "的值域之内, 必须在: " + range + "之内");
    }
}