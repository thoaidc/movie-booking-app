CREATE DATABASE IF NOT EXISTS `mb_order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT ENCRYPTION='N';
USE `mb_order`;

DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id` int NOT NULL AUTO_INCREMENT,
    `customer_id` int,
    `show_id` int NOT NULL,
    `total_amount` DECIMAL(10,2) NOT NULL,
    `create_time` timestamp NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (`id`),
    INDEX idx_order_customer (customer_id),
    INDEX idx_order_show (show_id),
    INDEX idx_order_status (status),
    INDEX idx_order_date (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `booking_seat`;
CREATE TABLE `booking_seat` (
    `id` int NOT NULL AUTO_INCREMENT,
    `order_id` int NOT NULL,
    `seat_id` int NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY uk_booking_seat (order_id, seat_id),
    INDEX idx_order_booking_item (order_id),
    CONSTRAINT fk_order_booking_seat FOREIGN KEY (order_id) REFERENCES `order`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
