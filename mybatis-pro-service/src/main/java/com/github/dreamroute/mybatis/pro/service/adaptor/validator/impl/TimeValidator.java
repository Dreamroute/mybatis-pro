package com.github.dreamroute.mybatis.pro.service.adaptor.validator.impl;

import com.github.dreamroute.mybatis.pro.service.adaptor.validator.Time;
import org.apache.commons.lang3.math.NumberUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * mysql timestamp范围：1971-01-01 08:00:01 到2038-01-19 11:14:07，本来是从00:00:00开始的，但是中国属于UTC时区，需要增加8小时，这里我们前后缩减一年，保证正确性
 * 
 * @author w.dehai
 *
 */
public class TimeValidator implements ConstraintValidator<Time, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // 如果为空，不做处理
        if (value == null || value.length() == 0) {
            return true;
        }

        // 如果不是数字，报错
        if (!NumberUtils.isCreatable(value)) {
            return false;
        }

        // 处理数字情况
        try {
            long from = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1972-01-01 00:00:00").getTime();
            long to = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2037-12-31 00:00:00").getTime();
            long time = Long.parseLong(value);
            if (time < from || time > to) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

}
