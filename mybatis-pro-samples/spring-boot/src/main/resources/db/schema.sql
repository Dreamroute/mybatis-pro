DROP TABLE IF EXISTS `smart_user`;

CREATE TABLE `smart_user111`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,
    `name`          varchar(32) DEFAULT NULL,
    `password`    varchar(32) DEFAULT NULL,
    `version`        bigint(20)  DEFAULT NULL,
    PRIMARY KEY (`id`)
)