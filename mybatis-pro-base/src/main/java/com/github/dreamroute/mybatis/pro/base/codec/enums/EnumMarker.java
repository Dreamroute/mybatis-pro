package com.github.dreamroute.mybatis.pro.base.codec.enums;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 枚举类型标记接口，实现此接口的枚举类型会被mybatis自动进行转型
 *
 * @author w.dehai
 */
public interface EnumMarker extends Serializable {

    /**
     * 返回枚举的value字段的值
     *
     * @return 返回枚举的value字段的值
     */
    Integer getValue();

    /**
     * 返回枚举描述信息
     *
     * @return 返回描述信息
     */
    String getDesc();

    /**
     * 根据value字段的值返回对应的枚举类型
     *
     * @param enumCls 枚举类型
     * @param value Integer类型的值
     * @return 返回具体的枚举类型
     */
    static <E extends EnumMarker> E valueOf(Class<E> enumCls, int value) {
        E[] enumConstants = enumCls.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getValue() == value) {
                return e;
            }
        }
        String valueList = Stream.of(enumConstants).map(EnumMarker::getValue).map(String::valueOf).collect(Collectors.joining(", ", "[", "]"));
        throw new IllegalArgumentException("枚举值[" + value + "]不在" + enumCls.getSimpleName() + "的取值范围" + valueList + "之内");
    }
}