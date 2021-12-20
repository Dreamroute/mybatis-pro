/**
 * 描述：此包是枚举EnumMarker的序列化和反序列化的jackson实现，
 * JacksonSerializer的枚举类型可以是EnumMarker，也可以是Enum类型，
 * 但是JacksonDeserializer的枚举类型就不能是EnumMarker，必须是Enum类型或者是具体的枚举类型，
 * 开发JacksonDeserializer的灵感来源于这里：https://www.cnblogs.com/kelelipeng/p/13972138.html，
 * 事实上也可以使用Fastjson方式的序列化和反序列化方式，但是Fastjson在前些年出过几次比较重大的漏洞时间，
 * 加上Fastjson源码不够优秀，并且基本上是个人开发者，而jackson是Spring默认的序列化和反序列化工具包。
 *
 * 使用方式：
 * 1. 可以自定义HttpMessageConverter实现WebMvcConfigurer的configureMessageConverters方法，例如：
 * <pre>
 * @Configuration
 * public class HttpMsgConverterConfig implements WebMvcConfigurer {
 *     @Override
 *     public void configureMessageConverters(@Nonnull List<HttpMessageConverter<?>> converters) {
 *         converters.removeIf(e -> e instanceof MappingJackson2HttpMessageConverter);
 *
 *         SimpleModule module = new SimpleModule();
 *         module.addSerializer(Enum.class, new JacksonSerializer());
 *         module.addDeserializer(Enum.class, new JacksonDeserializer());
 *
 *         ObjectMapper om = new ObjectMapper();
 *         om.registerModule(module);
 *
 *         MappingJackson2HttpMessageConverter c = new MappingJackson2HttpMessageConverter(om);
 *         c.setObjectMapper(om);
 *
 *         converters.add(0, c);
 *     }
 * }
 * </pre>
 * 2. 也可以不自定义HttpMessageConverter，直接在EnumMarker头顶上加上序列化和反序列化的注解，例如：
 * <pre>
 *@JsonSerialize(using = JacksonSerializer.class)
 * @JsonDeserialize(using = JacksonDeserializer.class)
 * public interface EnumMarker extends Serializable {}
 * </pre>
 *
 * @author w.dehi.2021-12-19
 */
package com.github.dreamroute.mybatis.pro.base.enums;