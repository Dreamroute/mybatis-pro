package com.github.dreamroute.mybatis.pro.core.consts;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author w.dehai
 */
@Data
@ConfigurationProperties(prefix = "mybatis.pro")
public class MyBatisProProperties {

    /**
     * 是否开启逻辑删除，true-开启，false-关闭，默认开启
     */
    private boolean enableLogicalDelete = true;

    /**
     * 逻辑删除备份表名，deleteUseUpdate为false时此属性才有意义
     */
    private String backupTable = "backup_table";

    /**
     * 逻辑删除，使用更改状态的方式
     */
    private boolean logicalDelete = false;

    /**
     * 逻辑删除状态列，deleteUseUpdate为true时此属性才有意义
     */
    private String logicalDeleteColumn = "status";

    /**
     * 逻辑删除数据有效状态，单表查询时会在sql的where条件上自动加上[AND ${logicalDeleteColumn} = ${logicalDeleteActive}]
     */
    private Integer logicalDeleteActive = 1;
}
