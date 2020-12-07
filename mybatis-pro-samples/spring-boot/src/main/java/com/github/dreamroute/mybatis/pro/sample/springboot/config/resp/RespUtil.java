package com.github.dreamroute.mybatis.pro.sample.springboot.config.resp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

/**
 * 返回给前端的数据结构
 *
 * @author w.dehai
 */
@Data
public final class RespUtil {

    @JSONField(ordinal = 1)
    private String code;
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
     * <pre>
     * {
     *    "code": "0",
     *    "desc": "请求成功",
     *    "data": <code>null</code>
     * }
     * </pre>
     *
     * @return 返回成功
     */
    public static String respSuccess() {
        return resp(RespEnum.SUCCESS);
    }

    /**
     * 请求成功：存在返回值
     *
     * <pre>
     * {
     *    "code": "0",
     *    "desc": "请求成功",
     *    "data": "数据"
     * }
     * </pre>
     *
     * @return 返回成功
     */
    public static String respSuccess(Object data) {
        return resp(data, RespEnum.SUCCESS);
    }

    public static String resp(RespEnum respEnum) {
        return resp(null, respEnum);
    }

    public static <T> String resp(T data, RespEnum respEnum) {
        RespUtil resp = new RespUtil(data, respEnum);
        SerializerFeature[] features = {
                // null字段也输出
                SerializerFeature.WriteMapNullValue,
                // List字段如果为null,输出为[],而非null
                SerializerFeature.WriteNullListAsEmpty,
                // 字符类型字段如果为null,输出为"",而非null
                SerializerFeature.WriteNullStringAsEmpty,
                // Boolean字段如果为null,输出为false,而非null
                SerializerFeature.WriteNullBooleanAsFalse,
                // 消除对同一对象循环引用的问题，默认为false
                SerializerFeature.DisableCircularReferenceDetect};
        return JSON.toJSONString(resp, features);
    }

}
