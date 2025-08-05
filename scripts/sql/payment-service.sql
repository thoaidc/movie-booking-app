CREATE DATABASE IF NOT EXISTS `mb_payment` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT ENCRYPTION='N';
USE `mb_payment`;

DROP TABLE IF EXISTS `payment_history`;
CREATE TABLE `payment_history` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `booking_id` INT NOT NULL,
    `transaction_id` VARCHAR(255),
    `amount` DECIMAL(10,2) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `payment_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_payment_booking (booking_id),
    INDEX idx_payment_status (status),
    INDEX idx_payment_transaction (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `refund_history`;
CREATE TABLE `refund_history` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `payment_id` INT NOT NULL,
    `transaction_id` VARCHAR(255),
    `amount` DECIMAL(10,2) NOT NULL,
    `reason` TEXT,
    `refund_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refund_payment FOREIGN KEY (payment_id) REFERENCES payment_history(id),
    INDEX idx_refund_payment (payment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
