package com.github.dreamroute.mybatis.pro.base.time;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

/**
 * 描述：日期序列化，将{@link java.util.Date}类型转换成'yyyy-MM-dd HH:mm:ss.SSS'
 *
 * @author w.dehi.2021-12-19
 */
public class DateSerializer extends JsonSerializer<Date> {

    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeString(DateUtil.format(value, FORMAT));
        }
    }
}
