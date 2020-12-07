package com.github.dreamroute.mybatis.pro.sample.springboot.util;

import cn.hutool.core.util.ReflectUtil;
import com.github.dreamroute.mybatis.pro.sample.springboot.domain.Dict;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.dreamroute.mybatis.pro.sample.springboot.util.DictFieldCache.findForClass;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * 字典工具类
 *
 * @author w.dehai
 */
public final class DictUtil {

    private DictUtil() {}

    /**
     * 字典List -> Map<enName, Map<value, cnName>>
     */
    public static Map<String, Map<Integer, String>> dict2Map(List<Dict> list) {
        Map<String, Map<Integer, String>> result = new HashMap<>(128);
        list.stream().collect(groupingBy(Dict::getEnName))
                .forEach((k, v) -> result.put(k, v.stream().collect(toMap(Dict::getValue, Dict::getLabelValue))));
        return result;
    }

    /**
     * 将带有@Dict标记的字段进行翻译：[state = 0] -> [stateDict = '有效']，无需手动对每个字典字段进行值的设置
     */
    @SuppressWarnings("unchecked")
    public static <K> K convert(K src, Map<String, Map<Integer, String>> dictMap) {
        Class<?> c = src.getClass();
        if (Collection.class.isAssignableFrom(c)) {
            Collection<?> cs = (Collection<?>) src;
            return (K) cs.stream().map(sr -> convertSingle(sr, dictMap)).collect(toList());
        }
        return convertSingle(src, dictMap);
    }

    private static <K> K convertSingle(K src, Map<String, Map<Integer, String>> dictMap) {
        Class<?> c = src.getClass();
        List<Field> fields = findForClass(c);
        for (Field field : fields) {
            Object value = ReflectUtil.getFieldValue(src, field);
            com.github.dreamroute.mybatis.pro.sample.springboot.util.anno.Dict d = field.getAnnotation(com.github.dreamroute.mybatis.pro.sample.springboot.util.anno.Dict.class);
            String lineName = d.value();
            if (lineName.length() == 0) {
                lineName = humpToLine(field.getName());
            }
            Map<Integer, String> nameMap = dictMap.get(lineName);
            String v = nameMap.get(value);
            Field prop = ReflectUtil.getField(c, field.getName() + "Dict");
            ReflectUtil.setFieldValue(src, prop, v);
        }
        return src;
    }

    /**
     * 驼峰转下划线
     *
     * @param src 驼峰
     * @return 返回下划线名称
     */
    public static String humpToLine(String src) {
        return src.replaceAll("[A-Z]", "_$0").toLowerCase();
    }
}
