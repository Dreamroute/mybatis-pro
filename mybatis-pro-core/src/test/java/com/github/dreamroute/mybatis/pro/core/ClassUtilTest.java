package com.github.dreamroute.mybatis.pro.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.Transient;

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
        List<String> names = ClassUtil.getFindByMethods(DemoMapper.class);
        String result = names.stream().collect(Collectors.joining(",", "[", "]"));
        Assertions.assertEquals("[findByName,findByNameAndPassword]", result);
    }

    @Test
    public void getAllFieldsTest() {
        Set<Field> allFields = ClassUtil.getAllFields(User.class);
        System.err.println(allFields);
    }

}

class M {
    @Transient
    private String name;
}
class User extends M {
    private static final long serialVersionUID = -1383742108573524072L;
    private Long id;
}
