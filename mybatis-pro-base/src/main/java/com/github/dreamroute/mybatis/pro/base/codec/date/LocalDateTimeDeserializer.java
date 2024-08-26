package com.github.dreamroute.mybatis.pro.base.codec.date;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamroute.mybatis.pro.base.codec.enums.EnumMarkerDeserializer;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 描述：日期反序列化，将'yyyy-MM-dd HH:mm:ss'反序列化成{@link java.time.LocalDateTime}类型
 *
 * @author w.dehi.2021-12-19
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String name = p.currentName();
        if (StrUtil.contains(name, "_")) {
            name = EnumMarkerDeserializer.underscoreToCamelCase(name);
        }
        Object obj = p.getCurrentValue();
        Class<?> propertyType = BeanUtils.findPropertyType(name, obj.getClass());
        if (LocalDateTime.class.isAssignableFrom(propertyType)) {
            String dateStr = p.getValueAsString();
            if (StrUtil.isNotBlank(dateStr)) {
                try {
                    return LocalDateTimeUtil.parse(dateStr, DatePattern.NORM_DATETIME_FORMATTER);
                } catch (Exception e) {
                    throw new IllegalArgumentException("日期格式错误, 当前日期为: " + dateStr + ", 需要yyyy-MM-dd HH:mm:ss格式");
                }
            }
        }
        return null;
    }
}
