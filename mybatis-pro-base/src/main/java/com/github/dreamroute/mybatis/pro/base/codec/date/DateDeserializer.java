package com.github.dreamroute.mybatis.pro.base.codec.date;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.Date;

import static com.github.dreamroute.mybatis.pro.base.codec.date.DateSerializer.FORMAT;

/**
 * 描述：日期反序列化，将'yyyy-MM-dd HH:mm:ss.SSS'反序列化成{@link java.util.Date}类型
 *
 * @author w.dehi.2021-12-19
 */
public class DateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String name = p.currentName();
        Object obj = p.getCurrentValue();
        Class<?> propertyType = BeanUtils.findPropertyType(name, obj.getClass());
        if (Date.class.isAssignableFrom(propertyType)) {
            String dateStr = p.getValueAsString();
            @SuppressWarnings("unchecked") Class<Date> c = (Class<Date>) propertyType;
            return DateUtil.parse(dateStr, FORMAT);
        }
        return null;
    }
}
