CREATE DATABASE IF NOT EXISTS `mb_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT ENCRYPTION='N';
USE `mb_user`;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` int NOT NULL AUTO_INCREMENT,
    `fullname` varchar(255) DEFAULT NULL,
    `username` varchar(50) NOT NULL,
    `email` varchar(100) NOT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `password` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX idx_user_email (email),
    INDEX idx_user_name (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
