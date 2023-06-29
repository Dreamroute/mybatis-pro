DROP TABLE IF EXISTS `smart_user`;
DROP TABLE IF EXISTS `smart_typehandler`;
DROP TABLE IF EXISTS `smart_dict`;
DROP TABLE IF EXISTS `logical_delete`;

CREATE TABLE `smart_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `password` varchar(32) DEFAULT '123456',
  `version` bigint(20) DEFAULT NULL,
  `phone_no` varchar(20) DEFAULT NULL,
  `addr_info` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE `smart_typehandler` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gender` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `smart_dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` tinyint(4) DEFAULT NULL,
  `label_value` varchar(50) DEFAULT NULL,
  `en_name` varchar(50) DEFAULT NULL,
  `cn_name` varchar(50) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `status` tinyint null,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE `logical_delete` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `table_name` varchar(100) NOT NULL,
  `data` longtext NOT NULL,
  `delete_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);
