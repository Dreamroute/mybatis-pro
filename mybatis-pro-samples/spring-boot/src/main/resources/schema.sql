DROP TABLE IF EXISTS `smart_user`;

CREATE TABLE `smart_user`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,
    `name`          varchar(32) DEFAULT NULL,
    `password`    varchar(32) DEFAULT '123456',
    `version`        bigint(20)  DEFAULT NULL,
    `phone_no`    varchar(20) DEFAULT NULL,
    `create_user` varchar(20) DEFAULT NULL,
    `create_time` varchar(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `backup_table`(
    `id` bigint(20) not null auto_increment primary key,
    `table_name` varchar(100) default null,
    `data` varchar(2000) default null
);
