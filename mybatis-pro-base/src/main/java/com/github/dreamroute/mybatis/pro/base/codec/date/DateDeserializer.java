package com.github.dreamroute.mybatis.pro.base.codec.date;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamroute.mybatis.pro.base.codec.PropertyAliasCache;
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
        String name = PropertyAliasCache.getFieldAliasMap(p);
        Class<?> propertyType = BeanUtils.findPropertyType(name, p.getCurrentValue().getClass());
        if (Date.class.isAssignableFrom(propertyType)) {
            String dateStr = p.getValueAsString();
            if (CharSequenceUtil.isNotBlank(dateStr)) {
                try {
                    return DateUtil.parse(dateStr, FORMAT);
                } catch (Exception e) {
                    throw new IllegalArgumentException("日期格式错误, 当前日期为: " + dateStr + ", 需要yyyy-MM-dd HH:mm:ss格式");
                }
            }
        }
        return null;
    }
}
