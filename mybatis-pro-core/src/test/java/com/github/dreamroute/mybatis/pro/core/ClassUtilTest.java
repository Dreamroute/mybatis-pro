package com.github.dreamroute.mybatis.pro.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.dreamroute.mybatis.pro.core.util.ClassUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.Transient;

/**
 * @author w.dehai
 */
class ClassUtilTest {

    @Test
    void getClassesFromPackagesTest() {
        Set<Class<?>> classes = ClassUtil.getClassesFromPackages(new HashSet<>(Arrays.asList("com.github.dreamroute.mybatis.pro.core")));
        System.err.println(classes);
    }

    @Test
    public void getInterfacesFromPackageTest() {
        Set<Class<?>> interfaces = ClassUtil.getInterfacesFromPackage(new HashSet<>(Arrays.asList("com.github.dreamroute.mybatis.pro.core")));
        System.err.println(interfaces);
    }

    @Test
    public void getName2TypeTest() {
        Map<String, String> name2Type = ClassUtil.getName2Type(DemoMapper.class);
        System.err.println(name2Type);
    }

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
