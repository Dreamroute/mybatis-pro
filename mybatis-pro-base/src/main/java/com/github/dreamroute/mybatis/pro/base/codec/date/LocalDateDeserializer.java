package com.github.dreamroute.mybatis.pro.base.codec.date;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamroute.mybatis.pro.base.codec.PropertyAliasCache;
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
        String name = PropertyAliasCache.getFieldAliasMap(p);
        Class<?> propertyType = BeanUtils.findPropertyType(name, p.getCurrentValue().getClass());
        if (LocalDate.class.isAssignableFrom(propertyType)) {
            String dateStr = p.getValueAsString();
            if (CharSequenceUtil.isNotBlank(dateStr)) {
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
