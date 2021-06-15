package com.github.dreamroute.mybatis.pro.core.consts;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.github.dreamroute.mybatis.pro.core.consts.MyBatisProProperties.DEFAULT_MYBATIS_PRO_PREFIX;

/**
 * @author w.dehai
 */
@Data
@ConfigurationProperties(prefix = DEFAULT_MYBATIS_PRO_PREFIX)
public class MyBatisProProperties {

    public static final String DEFAULT_MYBATIS_PRO_PREFIX = "mybatis.pro";
    public static final String DEFAULT_LOGICAL_DELETE_TYPE = "backup";
    public static final String DEFAULT_LOGICAL_TYPE_UPDATE = "update";
    public static final String DEFAULT_LOGICAL_DELETE_TABLE = "logical_delete_table";
    public static final String DEFAULT_LOGICAL_DELETE_COLUMN = "status";
    public static final Integer DEFAULT_LOGICAL_DELETE_ACTIVE = 1;
    public static final Integer DEFAULT_LOGICAL_DELETE_IN_ACTIVE = 0;


    /**
     * 是否开启逻辑删除，true-开启，false-关闭，默认开启
     */
    private boolean enableLogicalDelete = true;

    /**
     * 逻辑删除的方式：backup，update；backup：物理删除 + 备份；update：删除动作实际上是执行update操作，将状态位修改成为无效状态，默认是backup方式
     */
    private String logicalDeleteType = DEFAULT_LOGICAL_DELETE_TYPE;

    /**
     * 逻辑删除备份表名，只有当logicalType为backup时此属性才有意义
     */
    private String logicalDeleteTable = DEFAULT_LOGICAL_DELETE_TABLE;

    /**
     * 逻辑删除状态列，只有当logicalType为update时此属性才有意义
     */
    private String logicalDeleteColumn = DEFAULT_LOGICAL_DELETE_COLUMN;

    /**
     * 逻辑删除数据有效状态，单表查询时会在sql的where条件上自动加上[AND ${logicalDeleteColumn} = ${logicalDeleteActive}]
     */
    private Integer logicalDeleteActive = DEFAULT_LOGICAL_DELETE_ACTIVE;

    /**
     * 逻辑删除数据删除状态，进行逻辑删除时，实际上执行的是[update set ${logicalDeleteColumn} = ${logicalDeleteInActive}]
     */
    private Integer logicalDeleteInActive = DEFAULT_LOGICAL_DELETE_IN_ACTIVE;
}
