package com.github.dreamroute.mybatis.pro.base.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.dreamroute.mybatis.pro.base.EnumMarker;

import java.io.IOException;

/**
 * 描述：EnumMarker Jackson序列化
 *
 * @author w.dehi.2021-12-19
 */
@SuppressWarnings("rawtypes")
public class EnumMarkerSerializer extends JsonSerializer<Enum> {
    @Override
    public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof EnumMarker) {
            EnumMarker v = (EnumMarker) value;
            gen.writeObject(v.getValue());
        } else {
            gen.writeObject(value);
        }
    }
}
