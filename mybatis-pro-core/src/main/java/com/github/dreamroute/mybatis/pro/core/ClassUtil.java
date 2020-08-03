package com.github.dreamroute.mybatis.pro.core;

import org.springframework.util.ObjectUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    public static List<String> getFindByMethods(Class<?> interfaceCls) {
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

    /**
     * 获取接口的所有父接口
     */
    public static Set<Class<?>> getAllParentInterface(Class<?> cls) {
        Set<Class<?>> result = new HashSet<>();
        recursiveCls(cls, result);
        return result;
    }

    private static void recursiveCls(Class<?> cls, Set<Class<?>> result) {
        Class<?>[] interfaces = cls.getInterfaces();
        if (!ObjectUtils.isEmpty(interfaces)) {
            result.addAll(Arrays.asList(interfaces));
            Arrays.stream(interfaces).forEach(inter -> recursiveCls(inter, result));
        }
    }

    /**
     * 获取实体所有属性
     */
    public static Set<Field> getAllFields(Class<?> cls) {
        Set<Field> result = new HashSet<>();
        recursiveField(cls, result);

        // 过滤掉serialVersionUID和@javax.persistence.Transient属性
        return result.stream().filter(field -> !(Objects.equals(field.getName(), "serialVersionUID") || field.isAnnotationPresent(Transient.class))).collect(Collectors.toSet());
    }

    private static void recursiveField(Class<?> cls, Set<Field> result) {
        if (cls != Object.class) {
            Field[] declaredFields = cls.getDeclaredFields();
            if (!ObjectUtils.isEmpty(declaredFields)) {
                result.addAll(Arrays.asList(declaredFields));
                recursiveField(cls.getSuperclass(), result);
            }
        }
    }

    /**
     * 获取id字段
     */
    public static String getIdName(Class<?> cls) {
        Set<Field> idFields = getAllFields(cls).stream().filter(field -> field.isAnnotationPresent(Id.class)).collect(Collectors.toSet());
        if (idFields != null && idFields.size() != 1) {
            throw new MyBatisProException("实体" + cls.getName() + "缺少@Id注解标注的主键字段");
        }

        Field idField = idFields.iterator().next();
        Column colAnno = idField.getAnnotation(Column.class);

        return (colAnno != null && colAnno.name() != null) ? colAnno.name() : SqlUtil.toLine(idField.getName());
    }

}
