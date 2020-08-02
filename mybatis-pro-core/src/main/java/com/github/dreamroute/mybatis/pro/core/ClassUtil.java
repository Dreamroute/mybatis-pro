package com.github.dreamroute.mybatis.pro.core;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 查询接口的方法的返回值类型（包括普通类型和泛型类型），由于只需要容器初始化的时候执行，所以不需要缓存
 *
 * @author w.dehai
 */
public class ClassUtil {

    private ClassUtil() {}

    /**
     * 获取方法返回值类型
     *
     * @param method 方法
     * @return 返回值类型
     */
    public static String getReturnType(Method method) {
        Class<?> returnType = method.getReturnType();

        // 普通类型
        if (returnType != List.class)
            return returnType.getName();

        // List类型
        Type genericReturnType = method.getGenericReturnType();
        ParameterizedType pt = (ParameterizedType) genericReturnType;
        Type actualTypeArgument = pt.getActualTypeArguments()[0];
        String typeName = actualTypeArgument.getTypeName();
        return typeName;
    }

    /**
     * 返回Mapper接口的findBy开头的方法
     *
     * @param interfaceCls Mapper接口
     * @return findBy开头的方法的方法名字
     */
    public static List<String> getSpecialMethods(Class<?> interfaceCls) {
        Method[] methods = interfaceCls.getMethods();
        return Arrays.stream(methods).map(Method::getName).filter(name -> name.startsWith("findBy")).collect(Collectors.toList());
    }

    /**
     * 获取Mapper方法名与返回值对应的map
     *
     * @param interfaceCls 接口
     * @return 返回映射关系
     */
    public static Map<String, String> getName2Type(Class<?> interfaceCls) {
        Method[] ms = interfaceCls.getMethods();
        Map<String, String> result = new HashMap<>();
        if (ms != null && ms.length > 0) {
            for (Method method : ms) {
                result.put(method.getName(), getReturnType(method));
            }
        }
        return result;
    }

    public static List<Class<?>> getAllParentInterface(Class<?> cls) {
        List<Class<?>> all = new ArrayList<>();
        Class<?> superclass = cls.getSuperclass();
        Class<?> current = superclass;
        while (current != null) {
            
        }
        return all;
    }

}
