DROP TABLE IF EXISTS `smart_user`;

CREATE TABLE `smart_user`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,
    `name`          varchar(32) DEFAULT NULL,
    `password`    varchar(32) DEFAULT '123456',
    `version`        bigint(20)  DEFAULT NULL,
    `phone_no`    varchar(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
);
