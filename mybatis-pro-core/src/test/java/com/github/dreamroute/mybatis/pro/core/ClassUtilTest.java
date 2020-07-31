package com.github.dreamroute.mybatis.pro.core;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author w.dehai
 */
public class ClassUtilTest {

    @Test
    public void getReturnTypeTest() throws NoSuchMethodException, SecurityException {

        // 返回值为entity
        Method method1 = DemoMapper.class.getDeclaredMethod("findByNameAndPassword", String.class, String.class);
        // 返回值为List
        Method method2 = DemoMapper.class.getDeclaredMethod("findByName", String.class);

        String type1 = ClassUtil.getReturnType(method1);
        String type2 = ClassUtil.getReturnType(method2);

        Assertions.assertEquals("com.github.dreamroute.fast.mapper.sdk.Demo", type1);
        Assertions.assertEquals("com.github.dreamroute.fast.mapper.sdk.Demo", type2);
    }

    @Test
    public void getSpecialMethodsTest() {
        List<String> names = ClassUtil.getSpecialMethods(DemoMapper.class);
        String result = names.stream().collect(Collectors.joining(",", "[", "]"));
        Assertions.assertEquals("[findByName,findByNameAndPassword]", result);
    }

}
