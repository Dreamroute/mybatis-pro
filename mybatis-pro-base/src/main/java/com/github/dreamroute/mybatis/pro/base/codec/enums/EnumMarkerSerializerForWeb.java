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
@SuppressWarnings("rawtypes")
public class EnumMarkerSerializerForWeb extends JsonSerializer<Enum> {
    @Override
    public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof EnumMarker) {
            EnumMarker v = (EnumMarker) value;

            EnumObj eo = new EnumObj(v.getValue(), v.getDesc());
            gen.writeObject(eo);

            // -- 也可以使用下面方式输出给前端，效果一样
//            gen.writeStartObject();
//            gen.writeNumberField("value", v.getValue());
//            gen.writeStringField("desc", v.getDesc());
//            gen.writeEndObject();

        } else {
            gen.writeObject(value);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class EnumObj implements EnumMarker {
        private final Integer value;
        private final String desc;
    }
}

