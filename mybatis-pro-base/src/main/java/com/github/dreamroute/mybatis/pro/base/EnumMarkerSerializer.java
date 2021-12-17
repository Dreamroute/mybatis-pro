package com.github.dreamroute.mybatis.pro.base;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.BeanUtils;

import java.io.IOException;

/**
 * 参考：https://www.geek-share.com/detail/2789979246.html
 * jackson反序列化时候全局处理EnumMarker类型的枚举
 *
 * @author w.dehai.2021/8/10.14:43
 */
public class EnumMarkerSerializer extends JsonDeserializer<EnumMarker> {
    @Override
    public EnumMarker deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String name = p.currentName();
        Object value = p.getCurrentValue();
        Class<?> propertyType = BeanUtils.findPropertyType(name, value.getClass());
        if (EnumMarker.class.isAssignableFrom(propertyType)) {
            int intValue = p.getIntValue();
            Class<EnumMarker> c = (Class<EnumMarker>) propertyType;
            return EnumMarker.valueOf(c, intValue);
        }
        return null;
    }
}