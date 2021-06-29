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

    // 逻辑删除方式: backup
    public static final String LOGICAL_DELETE_TYPE_BACKUP = "backup";
    // 逻辑删除方式: update
    public static final String LOGICAL_DELETE_TYPE_UPDATE = "update";
    // 逻辑删除方式: 默认
    public static final String DEFAULT_LOGICAL_DELETE_TYPE = LOGICAL_DELETE_TYPE_BACKUP;
    // 逻辑删除备份表名:
    public static final String LOGICAL_DELETE_TABLE_NAME = "logical_delete_table";
    // 逻辑删除状态列名
    public static final String LOGICAL_DELETE_STATUS_COLUMN = "status";
    // 逻辑删除状态列有效值
    public static final Integer LOGICAL_DELETE_STATUS_ACTIVE = 1;
    // 逻辑删除状态列无效值
    public static final Integer LOGICAL_DELETE_STATUS_IN_ACTIVE = 0;

    /**
     * 是否使用{@link com.github.dreamroute.mybatis.pro.base.typehandler.EnumTypeHandler}这个枚举转换器
     */
    private boolean enableEnumTypeHandler;

    /**
     * 是否开启逻辑删除，true-开启，false-关闭，默认关闭
     */
    private boolean enableLogicalDelete;

    /**
     * 逻辑删除的方式：backup，update；backup：物理删除 + 备份；update：删除动作实际上是执行update操作，将状态位修改成为无效状态，默认是backup方式
     */
    private String logicalDeleteType = DEFAULT_LOGICAL_DELETE_TYPE;

    /**
     * 逻辑删除备份表名，只有当logical-delete-type为backup时此属性才有意义
     */
    private String logicalDeleteTable = LOGICAL_DELETE_TABLE_NAME;

    /**
     * 逻辑删除状态列，只有当logical-delete-type为update时此属性才有意义
     */
    private String logicalDeleteColumn = LOGICAL_DELETE_STATUS_COLUMN;

    /**
     * 逻辑删除数据有效状态，单表查询时会在sql的where条件上自动加上[AND ${logicalDeleteColumn} = ${logicalDeleteActive}]
     */
    private Integer logicalDeleteActive = LOGICAL_DELETE_STATUS_ACTIVE;

    /**
     * 逻辑删除数据删除状态，进行逻辑删除时，实际上执行的是[update set ${logicalDeleteColumn} = ${logicalDeleteInActive}]
     */
    private Integer logicalDeleteInActive = LOGICAL_DELETE_STATUS_IN_ACTIVE;
}
