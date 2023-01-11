DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
                        `username` varchar(32) NOT NULL COMMENT '用户名',
                        `nickname` varchar(32) NOT NULL COMMENT '昵称',
                        `password` varchar(128) NOT NULL COMMENT '密码',
                        `is_superuser` tinyint(2) DEFAULT '0' COMMENT '是否管理员：0-否，1-是',
                        `is_deleted` tinyint(2) DEFAULT '0' COMMENT '是否逻辑删除：0-否，1-是',
                        `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                        `updated_at` timestamp NULL DEFAULT NULL COMMENT '最近修改时间',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='用户表';