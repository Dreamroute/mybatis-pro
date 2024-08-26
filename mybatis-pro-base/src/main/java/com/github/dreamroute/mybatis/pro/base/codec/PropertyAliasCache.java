package com.github.dreamroute.mybatis.pro.base.codec;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述：企微对象别名缓存
 *
 * @author w.dehai.2024/8/26.11:16
 */
public class PropertyAliasCache {
    private PropertyAliasCache() {}

    private static final ConcurrentHashMap<Class<?>, Map<String, String>> CACHE = new ConcurrentHashMap<>();

    public static String getFieldAliasMap(JsonParser jsonParser) {
        Map<String, String> map = CACHE.computeIfAbsent(jsonParser.getCurrentValue().getClass(), c -> {
            Map<String, String> fieldMap = new HashMap<>();
            Field[] fields = ReflectUtil.getFields(c);
            for (Field field : fields) {
                String fieldName = field.getName();
                String annotationValue = AnnotationUtil.getAnnotationValue(field, JsonProperty.class);
                annotationValue = CharSequenceUtil.isBlank(annotationValue) ? fieldName : annotationValue;
                fieldMap.put(annotationValue, fieldName);
            }
            return fieldMap;
        });
        try {
            return map.get(jsonParser.getCurrentName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
