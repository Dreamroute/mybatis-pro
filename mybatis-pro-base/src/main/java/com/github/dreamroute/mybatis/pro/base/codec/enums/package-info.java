/**
 * <p>
 * 描述：此包是枚举EnumMarker的序列化和反序列化的jackson实现，
 * <p>
 * JacksonSerializer的枚举类型可以是EnumMarker，也可以是Enum类型，
 * <p>
 * 但是JacksonDeserializer的枚举类型就不能是EnumMarker，必须是Enum类型或者是具体的枚举类型，
 * <p>
 * 开发JacksonDeserializer的灵感来源于这里：<a href="https://www.cnblogs.com/kelelipeng/p/13972138.html">https://www.cnblogs.com/kelelipeng/p/13972138.html</a>，
 * <p>
 * 事实上也可以使用Fastjson方式的序列化和反序列化方式，但是Fastjson在前些年出过几次比较重大的漏洞时间，加上Fastjson源码不够优秀，并且基本上是个人开发者，而jackson是Spring默认的序列化和反序列化工具包。
 *<p>
 * 使用方式：
 * 1. 可以自定义HttpMessageConverter实现WebMvcConfigurer的configureMessageConverters方法，例如：
 * <pre>
 * &#64;Configuration
 * public class HttpMsgConverterConfig implements WebMvcConfigurer {
 *     &#64;Override
 *     public void configureMessageConverters(@Nonnull List<HttpMessageConverter<?>> converters) {
 *         converters.removeIf(e -> e instanceof MappingJackson2HttpMessageConverter);
 *
 *         SimpleModule module = new SimpleModule();
 *         module.addSerializer(Enum.class, new JacksonSerializer());
 *         module.addDeserializer(Enum.class, new JacksonDeserializer());
 *         simpleModule.addDeserializer(Collection.class, new EnumMarkerDeserializerForCollection());
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
 * &#64;JsonSerialize(using = JacksonSerializer.class)
 * &#64;JsonDeserialize(using = JacksonDeserializer.class)
 * public interface EnumMarker extends Serializable {}
 * </pre>
 *
 * 3. 对于枚举类型fastjson和jackson选型问题，fastjson存在的问题：
 * 1. 列表方式的枚举反序列化会oom；
 * 2. 枚举类型传<code>null</code>会有默认认知0造成业务出错，业务中如果允许Gender为空，前端传{"gender": null}，那么gender就是0对应的枚举，而jackson就不会
 * <pre>
 *     public class Demo {
 *         private Gender gender;
 *     }
 * </pre>
 * 3. 并且通过{@link com.github.dreamroute.mybatis.pro.base.codec.enums.EnumMarkerDeserializerForCollection}能够解决列表方式的枚举问题，而fastjson的oom就不太好解决
 *
 *
 * @author w.dehi.2021-12-19
 */
package com.github.dreamroute.mybatis.pro.base.codec.enums;