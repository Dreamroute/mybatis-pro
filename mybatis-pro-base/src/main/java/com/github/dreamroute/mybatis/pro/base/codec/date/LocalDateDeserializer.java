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
import java.time.LocalDate;

/**
 * 描述：日期反序列化，将'yyyy-MM-dd HH:mm:ss'反序列化成{@link java.time.LocalDate}类型
 *
 * @author w.dehi.2021-12-19
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String name = p.currentName();
        if (StrUtil.contains(name, "_")) {
            name = EnumMarkerDeserializer.underscoreToCamelCase(name);
        }
        Object obj = p.getCurrentValue();
        Class<?> propertyType = BeanUtils.findPropertyType(name, obj.getClass());
        if (LocalDate.class.isAssignableFrom(propertyType)) {
            String dateStr = p.getValueAsString();
            if (StrUtil.isNotBlank(dateStr)) {
                try {
                    return LocalDateTimeUtil.parseDate(dateStr, DatePattern.NORM_DATE_FORMATTER);
                } catch (Exception e) {
                    throw new IllegalArgumentException("日期格式错误, 当前日期为: " + dateStr + ", 需要yyyy-MM-dd格式");
                }
            }
        }
        return null;
    }
}
