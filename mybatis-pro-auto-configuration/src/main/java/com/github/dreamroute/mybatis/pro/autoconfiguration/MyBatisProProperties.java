package com.github.dreamroute.mybatis.pro.autoconfiguration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author w.dehai
 */
@Data
@ConfigurationProperties(prefix = "mybatis.pro")
public class MyBatisProProperties {
    /**
     * 逻辑删除备份表
     */
    private String backupTable = "backup_table";
}
