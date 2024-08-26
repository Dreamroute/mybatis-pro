package com.github.dreamroute.mybatis.pro.base.codec.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamroute.mybatis.pro.base.codec.PropertyAliasCache;
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
        String name = PropertyAliasCache.getFieldAliasMap(p);
        Class<?> propertyType = BeanUtils.findPropertyType(name, p.getCurrentValue().getClass());

        int intValue = p.getIntValue();
        @SuppressWarnings("unchecked")
        Class<EnumMarker> c = (Class<EnumMarker>) propertyType;
        EnumMarker enumMarker = EnumMarker.valueOf(c, intValue);

        @SuppressWarnings("unchecked")
        Enum<? extends EnumMarker> resp = (Enum<? extends EnumMarker>) enumMarker;
        return resp;
    }
}
