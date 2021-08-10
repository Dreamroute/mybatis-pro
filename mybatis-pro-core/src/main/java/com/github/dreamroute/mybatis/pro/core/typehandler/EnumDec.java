package com.github.dreamroute.mybatis.pro.core.typehandler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.BeanUtils;

import java.io.IOException;

/**
 * @author w.dehai.2021/8/9.18:06
 */
public class EnumDec extends JsonDeserializer<EnumMarker> {
    @Override
    public EnumMarker deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String name = p.currentName();
        Object value = p.getCurrentValue();
        Class<?> propertyType = BeanUtils.findPropertyType(name, value.getClass());
        if (EnumMarker.class.isAssignableFrom(propertyType)) {
            int intValue = p.getIntValue();
            Class c = propertyType;
            return (EnumMarker) EnumMarker.valueOf(c, intValue);
        }
        return null;
    }
}