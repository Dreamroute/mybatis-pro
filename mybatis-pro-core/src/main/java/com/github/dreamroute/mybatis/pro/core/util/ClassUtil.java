package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dreamroute.mybatis.pro.core.MyBatisProException;
import com.github.dreamroute.mybatis.pro.core.annotations.Column;
import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import com.github.dreamroute.mybatis.pro.core.annotations.Transient;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.io.ResolverUtil.IsA;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author w.dehai
 */
public class ClassUtil {

    private ClassUtil() {}

    /**
     * 根据包名获取包内的所有类
     *
     * @param packages 包名
     * @return 返回包内所有类
     */
    public static Set<Class<?>> getClassesFromPackages(Set<String> packages) {
        return Optional.ofNullable(packages).orElse(new HashSet<String>())
                .stream().map(pkgName -> {
                    ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
                    resolverUtil.find(new IsA(Object.class), pkgName);
                    return resolverUtil.getClasses();
                }).flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    /**
     * 根据报名获取包内的所有接口
     * @param packages 包名
     * @return 返回包内所有接口
     */
    public static Set<Class<?>> getInterfacesFromPackage(Set<String> packages) {
        return Optional.ofNullable(getClassesFromPackages(packages)).orElse(new HashSet<Class<?>>())
                .stream().filter(Class::isInterface)
                .collect(Collectors.toSet());
    }

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
     * 获取Mapper方法名与返回值对应的map, Map<method-name, return-type>
     *
     * @param interfaceCls 接口
     * @return 返回映射关系
     */
    public static Map<String, String> getMethodName2ReturnType(Class<?> interfaceCls) {
        Method[] ms = interfaceCls.getMethods();
        return Arrays.stream(ms).collect(Collectors.toMap(Method::getName, ClassUtil::getReturnType));
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
     * 获取实体所有属性, 除外
     */
    public static Set<Field> getAllFields(Class<?> cls) {
        Set<Field> result = new HashSet<>();
        recursiveField(cls, result);

        return result.stream()
                .filter(field -> !specialProp(field))
                .collect(Collectors.toSet());
    }

    /**
     * 判断是否是serialVersionUID或者@javax.persistence.Transient
     */
    public static boolean specialProp(Field field) {
        return Objects.equals(field.getName(), "serialVersionUID") || field.isAnnotationPresent(Transient.class);
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
     * 获取id字段(有@Column注解那么就是注解的name值)
     */
    public static String getIdColumn(Class<?> cls) {
        Field idField = getIdField(cls);
        Column colAnno = idField.getAnnotation(Column.class);
        return (colAnno != null && colAnno.name() != null) ? colAnno.name() : SqlUtil.toLine(idField.getName());
    }

    /**
     * 获取id属性的属性名
     */
    public static String getIdName(Class<?> cls) {
        Field idField = getIdField(cls);
        return idField.getName();
    }

    private static Field getIdField(Class<?> cls) {
        Set<Field> idFields = getAllFields(cls).stream().filter(field -> field.isAnnotationPresent(Id.class)).collect(Collectors.toSet());
        if (idFields != null && idFields.size() != 1) {
            throw new MyBatisProException("实体" + cls.getName() + "缺少@Id注解标注的主键字段");
        }

        return idFields.iterator().next();
    }

    /**
     * 根据实体获取表名
     *
     * @param entityStr 实体
     * @return 返回表名
     */
    public static String getTableNameFromEntity(String entityStr) {
        try {
            Class<?> entityCls = ClassUtils.forName(entityStr, null);
            Table table = entityCls.getAnnotation(Table.class);
            return table.name();
        } catch (Exception e) {
            throw new IllegalArgumentException("获取表名失败，entity需要本@Table注解标注", e);
        }
    }

    /**
     * 获取接口Mapper<T, ID>的泛型真实类型
     */
    public static String getMapperGeneric(Class<?> mapperCls) {
        Type[] genericInterfaces = mapperCls.getGenericInterfaces();
        ParameterizedType pt = (ParameterizedType) genericInterfaces[0];
        Type[] args = pt.getActualTypeArguments();
        return args[0].getTypeName();
    }

}
