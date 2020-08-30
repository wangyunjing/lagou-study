CREATE SCHEMA `test` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TABLE `tb_resume` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL,
  `address` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL,
  `phone` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `test`.`tb_resume` (`name`, `address`, `phone`) VALUES ('张三', '上海', '111');
INSERT INTO `test`.`tb_resume` (`name`, `address`, `phone`) VALUES ('李四', '北京', '222');
INSERT INTO `test`.`tb_resume` (`name`, `address`, `phone`) VALUES ('王五', '上海', '333');
INSERT INTO `test`.`tb_resume` (`name`, `address`, `phone`) VALUES ('赵六', '香港', '444');