package com.github.dreamroute.mybatis.pro.base.codec.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * 描述：EnumMarker Jackson列表反序列化，DTO如果需要使用列表枚举，需要定义成<code>Collection<E>类型</code>，
 * 如果这里使用{@link java.util.Set}或者{@link java.util.List}作为泛型，那么除了{@link EnumMarker}之外的数组、列表类型也会进入此方法，
 * 所以必须使用Collection，在DTO中的定义如下：
 * <pre>
 *     &#64;Data
 *     public class DemoDto {
 *
 *         // 使用此方式
 *         private Collection&lt;Gender&gt; genders;
 *
 *         // 不要使用此方式
 *         private List&lt;Gender&gt; genders;
 *
 *     }
 * </pre>
 *
 * @author w.dehi.2021-12-19
 */
public class EnumMarkerDeserializerForCollection extends JsonDeserializer<Collection<Enum<? extends EnumMarker>>> {

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Enum<? extends EnumMarker>> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ArrayNode treeNode = p.readValueAsTree();
        Field field;
        try {
            field = p.getCurrentValue().getClass().getDeclaredField(p.currentName());
        } catch (NoSuchFieldException e) {
            return null;
        }
        field.setAccessible(true);
        if (!field.getType().equals(Collection.class)) {
            return null;
        }
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        Class<? extends EnumMarker> actualTypeArgument = (Class<? extends EnumMarker>) genericType.getActualTypeArguments()[0];
        @SuppressWarnings("rawtypes") Collection result = new ArrayList<>();
        Iterator<JsonNode> elements = treeNode.elements();
        while (elements.hasNext()) {
            String v = elements.next().asText();
            if (EnumMarker.class.isAssignableFrom(actualTypeArgument)) {
                EnumMarker enumMarker = EnumMarker.valueOf(actualTypeArgument, Integer.parseInt(v));
                result.add(enumMarker);
            } else {
                result.add(v);
            }
        }
        return result;
    }
}
