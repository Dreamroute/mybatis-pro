package com.github.dreamroute.mybatis.pro.base.enums;

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
public class EnumMarkerDeserializer extends JsonDeserializer<Enum<?>> {
    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String name = p.currentName();
        Object obj = p.getCurrentValue();
        Class<?> propertyType = BeanUtils.findPropertyType(name, obj.getClass());
        if (EnumMarker.class.isAssignableFrom(propertyType)) {
            int intValue = p.getIntValue();
            @SuppressWarnings("unchecked") Class<EnumMarker> c = (Class<EnumMarker>) propertyType;
            return (Enum<?>) EnumMarker.valueOf(c, intValue);
        }
        return null;
    }
}
