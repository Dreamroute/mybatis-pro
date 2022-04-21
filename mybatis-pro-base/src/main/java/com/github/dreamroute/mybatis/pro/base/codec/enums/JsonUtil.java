package com.github.dreamroute.mybatis.pro.base.codec.enums;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.dreamroute.mybatis.pro.base.codec.date.DateDeserializer;
import com.github.dreamroute.mybatis.pro.base.codec.date.DateSerializer;

import java.util.Date;
import java.util.List;

/**
 * 描述：Json工具类，枚举类型/日期类型的序列化、反序列化工具类，将EnumMaker的实现类（枚举类型）进行如下操作：
 * <ol>
 *     <li>枚举序列化：EnumMarker.getValue()</li>
 *     <li>枚举反序列化：EnumMarker.valueOf(value)</li>
 *     <li>日期序列化：Date -> yyyy-MM-dd HH:mm:ss.SSS</li>
 *     <li>日期反序列化：yyyy-MM-dd HH:mm:ss.SSS -> Date</li>
 * </ol>
 * 举例：
 * <pre>
 *     // 性别（Gender）枚举类型
 *     public enum Gender implements EnumMarker {
 *         MALE(1, "男"),
 *         FEMALE(2, "女");
 *         private final Integer value;
 *         private final String desc;
 *     }
 *
 *     // Java实体类
 *     public class User {
 *         private String name;
 *         private Gender gender;
 *     }
 *
 * </pre>
 *
 * @author w.dehi.2021-12-19
 */
public class JsonUtil {
    private JsonUtil() {}

    private static final ObjectMapper MAPPER = new JsonMapper();
    private static final ObjectMapper MAPPER_FOR_WEB = new JsonMapper();

    static {

        EnumMarkerDeserializer emd = new EnumMarkerDeserializer();
        DateSerializer ds = new DateSerializer();
        DateDeserializer dd = new DateDeserializer();

        SimpleModule module = new SimpleModule();
        module.addSerializer(Enum.class, new EnumMarkerSerializer());
        module.addDeserializer(Enum.class, emd);

        module.addSerializer(Date.class, ds);
        module.addDeserializer(Date.class, dd);
        MAPPER.registerModule(module);

        SimpleModule moduleForWeb = new SimpleModule();
        moduleForWeb.addSerializer(Enum.class, new EnumMarkerSerializerForWeb());
        moduleForWeb.addDeserializer(Enum.class, emd);

        moduleForWeb.addSerializer(Date.class, ds);
        moduleForWeb.addDeserializer(Date.class, dd);
        MAPPER_FOR_WEB.registerModule(moduleForWeb);
    }

    /**
     * 序列化对象：将对象转换成json字符串
     *
     * <pre>
     *     {
     *         "name": "w.dehi",
     *         "gender": EnumMarkder.getValue()
     *     }
     *  </pre>
     *
     * @param target pojo对象
     * @return 返回json字符串
     */
    public static String toJsonStr(Object target) {
        try {
            return MAPPER.writeValueAsString(target);
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
     * @param target pojo对象
     * @return 返回json字符串
     */
    public static String toJsonStrForWeb(Object target) {
        try {
            return MAPPER_FOR_WEB.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("序列化失败: ", e);
        }
    }

    /**
     * 反序列化对象：将字符串转换成pojo对象
     * <pre>
     *     Json字符串：
     *     {
     *         "name": "w.deahi",
     *         "gender": 1
     *     }
     *
     * </pre>
     *
     * @param inputJson json字符串
     * @param clazz pojo类型
     */
    public static <T> T parseObj(String inputJson, Class<T> clazz) {
        try {
            return MAPPER.readValue(inputJson, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("反序列化失败, 需要被反序列化的字符串: " + inputJson, e);
        }
    }

    /**
     * 反序列化列表：将字符串转换成pojo列表
     *
     * @param inputJson json字符串
     * @param clazz pojo类型
     */
    public static <T> List<T> parseArr(String inputJson, Class<T> clazz) {
        try {
            CollectionType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            return MAPPER.readValue(inputJson, javaType);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("反序列化失败, 需要被反序列化的字符串: " + inputJson, e);
        }
    }
}
