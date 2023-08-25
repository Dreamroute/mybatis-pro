package com.github.dreamroute.mybatis.pro.base.codec.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * 描述：EnumMarker Jackson序列化，将EnumMarkder序列化成对象方式，用途是返回给前端页面使用:
 * <pre>
 *     {
 *         "name": "w.dehi",
 *         "gender": {
 *             "value": EnumMarker.getValue(),
 *             "desc": EnumMarker.getDesc()
 *         }
 *     }
 * </pre>
 *
 * @author w.dehi.2021-12-19
 */
public class EnumMarkerSerializerForWeb extends JsonSerializer<EnumMarker> {
    public EnumMarkerSerializerForWeb() {
    }

    public void serialize(EnumMarker value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(new EnumObj(value.getValue(), value.getDesc()));
        // -- 也可以使用下面方式输出给前端，效果一样
//            gen.writeStartObject();
//            gen.writeNumberField("value", v.getValue());
//            gen.writeStringField("desc", v.getDesc());
//            gen.writeEndObject();
    }

    @Getter
    @RequiredArgsConstructor
    private static class EnumObj {
        private final Integer value;
        private final String desc;
    }
}