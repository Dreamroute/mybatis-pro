package com.github.dreamroute.mybatis.pro.base.codec.date;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 描述：日期序列化，将{@link java.time.LocalDateTime}类型转换成'yyyy-MM-dd HH:mm:ss'
 *
 * @author w.dehi.2021-12-19
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeString(LocalDateTimeUtil.formatNormal(value));
        }
    }
}
