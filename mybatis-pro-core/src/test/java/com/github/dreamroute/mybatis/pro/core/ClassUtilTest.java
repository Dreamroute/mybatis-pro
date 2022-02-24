package com.github.dreamroute.mybatis.pro.core;

import com.github.dreamroute.mybatis.pro.core.annotations.Transient;
import com.github.dreamroute.mybatis.pro.core.util.ClassUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author w.dehai
 */
class ClassUtilTest {

    @Test
    void getName2TypeTest() {
        Map<String, String> name2Type = ClassUtil.getMethodName2ReturnType(DemoMapper.class);
        assertEquals(5, name2Type.size());
    }

    @Test
    void getReturnTypeTest() throws NoSuchMethodException, SecurityException {

        // 返回值为entity
        Method method1 = DemoMapper.class.getDeclaredMethod("findByNameAndPassword", String.class, String.class);
        // 返回值为List
        Method method2 = DemoMapper.class.getDeclaredMethod("findByName", String.class);

        String type1 = ClassUtil.getReturnType(method1);
        String type2 = ClassUtil.getReturnType(method2);

        assertEquals("com.github.dreamroute.mybatis.pro.core.Demo", type1);
        assertEquals("com.github.dreamroute.mybatis.pro.core.Demo", type2);
    }

    @Test
    void getSpecialMethodsTest() {
        List<String> names = ClassUtil.getSpecialMethods(DemoMapper.class);
        String result = names.stream().sorted().collect(Collectors.joining(",", "[", "]"));
        assertEquals("[findById,findByName,findByNameAndPassword]", result);
    }

    @Test
    void getAllFieldsTest() {
        Set<Field> allFields = ClassUtil.getAllFields(User.class);
        assertEquals(1, allFields.size());
    }

    @Test
    void getInnerMethodNamesTest() {
        Set<String> methodNames = ClassUtil.getBaseMethodNames();
        Assertions.assertNotNull(methodNames);
    }

}

class M {
    @Transient
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class User extends M implements Serializable {
    private static final long serialVersionUID = -1383742108573524072L;
    private Long id;

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

}
