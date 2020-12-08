package com.github.dreamroute.mybatis.pro.sample.springboot.config.resp;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

import static com.alibaba.fastjson.JSON.toJSONString;
import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteNullBooleanAsFalse;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteNullListAsEmpty;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteNullStringAsEmpty;
import static com.github.dreamroute.mybatis.pro.sample.springboot.config.resp.RespEnum.SUCCESS;

/**
 * 返回给前端的数据结构
 *
 * @author w.dehai
 */
@Data
public final class RespUtil {

    @JSONField(ordinal = 1)
    private Integer code;
    @JSONField(ordinal = 2)
    private String desc;
    @JSONField(ordinal = 3)
    private Object data;

    private RespUtil(Object data, RespEnum re) {
        this.data = data;
        this.code = re.getCode();
        this.desc = re.getDesc();
    }

    /**
     * 请求成功：无返回值
     *
     * @return 返回成功
     */
    public static String respSuccess(Object data) {
        return resp(data, SUCCESS);
    }

    public static String resp(RespEnum respEnum) {
        return resp(null, respEnum);
    }

    public static <T> String resp(T data, RespEnum respEnum) {
        RespUtil resp = new RespUtil(data, respEnum);
        SerializerFeature[] features = {
                // null字段也输出
                WriteMapNullValue,
                // List字段如果为null, 输出为[], 而非null
                WriteNullListAsEmpty,
                // 字符类型字段如果为null, 输出为"", 而非null
                WriteNullStringAsEmpty,
                // Boolean字段如果为null, 输出为false, 而非null
                WriteNullBooleanAsFalse,
                // 消除对同一对象循环引用的问题, 默认为false
                DisableCircularReferenceDetect
        };
        return toJSONString(resp, features);
    }

}
