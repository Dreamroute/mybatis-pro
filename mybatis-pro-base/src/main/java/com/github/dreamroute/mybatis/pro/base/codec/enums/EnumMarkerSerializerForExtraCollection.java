package com.github.dreamroute.mybatis.pro.base.codec.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Collection;

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
public class EnumMarkerSerializerForExtraCollection extends JsonSerializer<Collection> {
    @Override
    public void serialize(Collection value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof EnumMarker) {
            EnumMarker v = (EnumMarker) value;
            gen.writeObject(v.getValue());
            String currentName = gen.getOutputContext().getCurrentName();
            if (CharSequenceUtil.isBlank(currentName)) {
                currentName = gen.getOutputContext().getParent().getCurrentName();
            }
            gen.writeObjectField(currentName + "Desc", v.getDesc());
        } else if (value instanceof Collection) {
            if (CollUtil.isNotEmpty(value)) {
                value.stream().findAny().filter(e -> e instanceof EnumMarker).ifPresent(e -> {
                    throw new IllegalArgumentException("返回值不允许是枚举类型Enumarker的集合类型, 因为无法增加Desc字段");
                });
            }
        } else {
            gen.writeObject(value);
        }
    }
}
