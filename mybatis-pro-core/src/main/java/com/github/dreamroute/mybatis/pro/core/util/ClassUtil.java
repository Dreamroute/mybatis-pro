package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import com.github.dreamroute.mybatis.pro.core.annotations.Transient;
import com.github.dreamroute.mybatis.pro.core.exception.MyBatisProException;
import com.github.dreamroute.mybatis.pro.sdk.Mapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ReflectUtil.getFields;
import static com.github.dreamroute.mybatis.pro.base.enums.JsonUtil.toJsonStr;
import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.springframework.core.annotation.AnnotatedElementUtils.hasAnnotation;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
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
        if (returnType != List.class) {
            return returnType.getName();
        }

        // List类型
        Type genericReturnType = method.getGenericReturnType();
        ParameterizedType pt = (ParameterizedType) genericReturnType;
        Type actualTypeArgument = pt.getActualTypeArguments()[0];
        return actualTypeArgument.getTypeName();
    }

    /**
     * 返回Mapper接口的xxxBy开头的方法
     *
     * @param interfaceCls Mapper接口
     * @return findBy开头的方法的方法名字
     */
    public static List<String> getSpecialMethods(Class<?> interfaceCls) {
        Method[] methods = interfaceCls.getDeclaredMethods();
        List<String> names = new ArrayList<>();
        for (Method m : methods) {
            String name = m.getName();
            boolean isBy = name.startsWith("findBy") || name.startsWith("updateBy") || name.startsWith("deleteBy") || name.startsWith("countBy") || name.startsWith("existBy");
            boolean isAnnoCrud = hasAnnotation(m, Select.class) || hasAnnotation(m, Update.class) || hasAnnotation(m, Insert.class) || hasAnnotation(m, Delete.class);
            if (isBy && isAnnoCrud) {
                throw new MyBatisProException("接口方法" + interfaceCls.getName() + "." + name +
                        "是[findBy, updateBy, deleteBy, countBy, existBy]之一, 会被MybatisPro框架自动创建SQL语句，不能使用[@Select, @Update, @Insert, @Delete]来自定义SQL，请对方法重新命名");
            }
            if (isBy) {
                names.add(name);
            }
        }
        return names;
    }



    /**
     * 获取Mapper的所有方法名
     */
    public static Set<String> getBaseMethodNames() {
        Set<Class<?>> parentInterfaces = getAllParentInterface(Mapper.class);
        return parentInterfaces.stream()
                .map(Class::getDeclaredMethods)
                .flatMap(Arrays::stream)
                .map(Method::getName)
                .collect(toSet());
    }

    /**
     * 获取Mapper方法名与返回值对应的map, Map<method-name, return-type>
     *
     * @param interfaceCls 接口
     * @return 返回映射关系
     */
    public static Map<String, String> getMethodName2ReturnType(Class<?> interfaceCls) {
        Method[] ms = interfaceCls.getMethods();
        Map<String, Long> methodCount = stream(ms).map(Method::getName).collect(groupingBy(identity(), counting()));
        Map<String, Long> duplicateMethods = methodCount.entrySet().stream().filter(count -> count.getValue() != 1L).collect(toMap(Entry::getKey, Entry::getValue));
        if (!isEmpty(duplicateMethods)) {
            throw new MyBatisProException(interfaceCls.getName() + "的方法: " +  toJsonStr(duplicateMethods.keySet()) + "存在重复的方法名，MyBatis-Pro不允许Mapper存在同名方法");
        }
        return stream(ms).collect(toMap(Method::getName, ClassUtil::getReturnType));
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
        Field[] fs = getFields(cls);
        return Arrays.stream(fs)
                .filter(ClassUtil::isBeanProp)
                .collect(Collectors.toSet());
    }

    /**
     * 判断是否是普通属性，（serialVersionUID或者@Transient）除外
     */
    public static boolean isBeanProp(Field field) {
        return !Objects.equals(field.getName(), "serialVersionUID") && !hasAnnotation(field, Transient.class);
    }

    public static Field getIdField(Class<?> cls) {
        Set<Field> idFields = getAllFields(cls).stream().filter(field -> field.isAnnotationPresent(Id.class)).collect(Collectors.toSet());
        if (idFields.size() != 1) {
            throw new MyBatisProException("实体" + cls.getName() + "必须要有且仅有一个被@Id注解标注的主键字段");
        }
        return idFields.iterator().next();
    }
}

