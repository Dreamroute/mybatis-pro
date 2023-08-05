package com.github.dreamroute.mybatis.pro.base.codec.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 描述：EnumMarker Jackson序列化，将EnumMarkder序列化成getValue()，并且为对象增加一个以Desc结尾的枚举字段方式：
 * <pre>
 *     {
 *         "name": "w.dehai",
 *         "gender": 1,
 *     }
 *     那么输出(嵌套对象也会添加一个字段)：
 *     {
 *         "name": "w.dehi",
 *         "gender": 1,
 *         "genderDesc": "男"
 *     }
 * </pre>
 *
 * @author w.dehi.2021-12-19
 */
@SuppressWarnings("rawtypes")
public class EnumMarkerSerializerForExtra extends JsonSerializer<Enum> {
    @Override
    public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof EnumMarker) {
            EnumMarker v = (EnumMarker) value;
            gen.writeObject(v.getValue());
            String currentName = gen.getOutputContext().getCurrentName();
            gen.writeObjectField(currentName + "Desc", v.getDesc());
        } else {
            gen.writeObject(value);
        }
    }
}
