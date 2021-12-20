package com.github.dreamroute.mybatis.pro.base.enums;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.List;

/**
 * 描述：序列化、反序列化工具类，默认将EnumMaker序列化成EnumMarker.getValue()，根据value反序列化成为EnumMarker
 *
 * @author w.dehi.2021-12-19
 */
public class JsonUtil {
    private JsonUtil() {}

    private static final ObjectMapper MAPPER = new JsonMapper();
    private static final ObjectMapper MAPPER_FOR_WEB = new JsonMapper();

    static {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Enum.class, new EnumMarkerSerializer());
        module.addDeserializer(Enum.class, new EnumMarkerDeserializer());
        MAPPER.registerModule(module);

        SimpleModule moduleForWeb = new SimpleModule();
        moduleForWeb.addSerializer(Enum.class, new EnumMarkerSerializerForWeb());
        moduleForWeb.addDeserializer(Enum.class, new EnumMarkerDeserializer());
        MAPPER_FOR_WEB.registerModule(moduleForWeb);
    }

    /**
     * 序列化对象：将对象转换成json字符串
     *
     * <pre>
     *      {
     *          "name": "w.dehi",
     *          "gender": EnumMarkder.getValue()
     *      }
     *  </pre>
     *
     * @param object pojo对象
     * @return 返回json字符串
     */
    public static String toJsonStr(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("序列化失败: ", e);
        }
    }

    /**
     * 序列化对象：将对象转换成json字符串
     * <pre>
     *       {
     *           "name": "w.dehi",
     *           "gender": {
     *               "value": EnumMarker.getValue(),
     *               "desc": EnumMarker.getDesc()
     *           }
     *       }
     *   </pre>
     *
     * @param object pojo对象
     * @return 返回json字符串
     */
    public static String toJsonStrForWeb(Object object) {
        try {
            return MAPPER_FOR_WEB.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("序列化失败: ", e);
        }
    }

    /**
     * 反序列化对象：将字符串转换成pojo对象
     *
     * @param input json字符串
     * @param clazz pojo类型
     */
    public static <T> T parseObj(String input, Class<T> clazz) {
        try {
            return MAPPER.readValue(input, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("反序列化失败, 需要被反序列化的字符串: " + input, e);
        }
    }

    /**
     * 反序列化列表：将字符串转换成pojo列表
     *
     * @param input json字符串
     * @param clazz pojo类型
     */
    public static <T> List<T> parseArr(String input, Class<T> clazz) {
        try {
            CollectionType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            return MAPPER.readValue(input, javaType);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("反序列化失败, 需要被反序列化的字符串: " + input, e);
        }
    }
}
