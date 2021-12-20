package com.github.dreamroute.mybatis.pro.base.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
        } else {
            gen.writeObject(value);
        }
    }

}

@Getter
@AllArgsConstructor
class EnumObj implements EnumMarker {
    private final Integer value;
    private final String desc;
}
