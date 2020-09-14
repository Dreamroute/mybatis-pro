package com.github.dreamroute.mybatis.pro.core;

import cn.hutool.core.util.ReflectUtil;
import com.github.dreamroute.mybatis.pro.core.util.ClassUtil;
import org.junit.jupiter.api.Test;

import javax.persistence.Transient;
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
        assertEquals(13, name2Type.size());
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
        String result = names.stream().collect(Collectors.joining(",", "[", "]"));
        assertEquals("[findByName,findByNameAndPassword,findById]", result);
    }

    @Test
    void getAllFieldsTest() {
        Set<Field> allFields = ClassUtil.getAllFields(User.class);
        assertEquals(2, allFields.size());

        Field[] fields = ReflectUtil.getFields(User.class);

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
