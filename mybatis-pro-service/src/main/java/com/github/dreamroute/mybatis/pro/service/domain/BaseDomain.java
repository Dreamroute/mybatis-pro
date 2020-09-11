package com.github.dreamroute.mybatis.pro.service.domain;

import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.sql.Timestamp;

/**
 * <p> @author w.dehai
 *
 * <p>Description: 基础实体类，几乎所有实力均需要继承自此
 * <p>对lombok注解的说明：
 * <ol>
 *  <li>@Data = {<code>@Getter</code>, <code>@Setter</code>, <code>@toString</code>}
 *  <li><code>@EqualsAndHashCode(onlyExplicitlyIncluded = true)</code>表名只针对<code>@EqualsAndHashCode.Include</code>标记的字段生效
 *  <li>在id字段上标记<code>@EqualsAndHashCode.Include</code>，表示实体对象的equals方法和hashCode方法只与主键id相关，与其他字段无关
 *  <li>业务实体需要继此类
 *  <li>业务实体需要如下使用注解（定义实体时请复制下列注解），参考(Demo.java)生成的equals和hashCode方法才只与id相关
 *  <pre>
 *      -@Data
 *      -@SuperBuilder
 *      -@NoArgsConstructor
 *      -@AllArgsConstructor
 *      -@Table(name = "表名")
 *      -@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
 *  </pre>
 * </ol>
 *
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BaseDomain {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Timestamp createTime;
    private String createUser;

}
