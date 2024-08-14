package com.github.dreamroute.mybatis.pro.base.codec.enums;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.BeanUtils;

import java.io.IOException;

/**
 * 描述：EnumMarker Jackson反序列化
 *
 * @author w.dehi.2021-12-19
 */
public class EnumMarkerDeserializer extends JsonDeserializer<Enum<? extends EnumMarker>> {
    @Override
    public Enum<? extends EnumMarker> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String name = p.currentName();
        if (StrUtil.contains(name, "_")) {
            name = underscoreToPascalCase(name);
        }
        Object obj = p.getCurrentValue();
        Class<?> propertyType = BeanUtils.findPropertyType(name, obj.getClass());

        int intValue = p.getIntValue();
        @SuppressWarnings("unchecked")
        Class<EnumMarker> c = (Class<EnumMarker>) propertyType;
        EnumMarker enumMarker = EnumMarker.valueOf(c, intValue);

        @SuppressWarnings("unchecked")
        Enum<? extends EnumMarker> resp = (Enum<? extends EnumMarker>) enumMarker;
        return resp;
    }

    /**
     * 将下划线命名转换为驼峰命名
     *
     * @param str 下划线命名的字符串
     * @return 驼峰命名的字符串
     */
    public static String underscoreToCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

    /**
     * 将下划线命名转换为首字母大写的驼峰命名
     *
     * @param str 下划线命名的字符串
     * @return 首字母大写的驼峰命名字符串
     */
    public static String underscoreToPascalCase(String str) {
        String camelCase = underscoreToCamelCase(str);
        if (camelCase != null && !camelCase.isEmpty()) {
            return camelCase.substring(0, 1).toUpperCase() + camelCase.substring(1);
        }
        return camelCase;
    }
}
