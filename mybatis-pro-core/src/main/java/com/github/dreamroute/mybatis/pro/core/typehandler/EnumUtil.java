package com.github.dreamroute.mybatis.pro.core.typehandler;

import com.github.dreamroute.mybatis.pro.core.exception.MyBatisProException;

/**
 * 根据枚举的值创建枚举对象
 *
 * @author w.dehai
 */
public class EnumUtil {

    public static <E extends Enum<?> & EnumMarker> E valueOf(Class<E> enumCls, int value) {
        E[] enumConstants = enumCls.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getValue() == value)
                return e;
        }
        throw new MyBatisProException("您传入的枚举值[" + value + "]不在" + enumCls.getName() + "的值域之内");
    }

}
