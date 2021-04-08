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
     * 逻辑删除备份表名，deleteUseUpdate为false时此属性才有意义
     */
    private String backupTable = "backup_table";

    /**
     * 逻辑删除，使用更改状态的方式
     */
    private boolean deleteUseUpdate = true;

    /**
     * 逻辑删除状态列，deleteUseUpdate为true时此属性才有意义
     */
    private String markColumn = "commonStatus";

    /**
     * 数据被删除
     */
    private Integer markDelete = -999;

    /**
     * 数据有效
     */
    private Integer markExist = 1;
}
