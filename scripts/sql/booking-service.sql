CREATE DATABASE IF NOT EXISTS `mb_booking` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT ENCRYPTION='N';
USE `mb_booking`;

DROP TABLE IF EXISTS `show`;
CREATE TABLE `show` (
    `id` int NOT NULL AUTO_INCREMENT,
    `movie_id` int NOT NULL,
    `cinema_room_id` int NOT NULL,
    `ticket_price` DECIMAL(10,2) NOT NULL,
    `start_time` timestamp not null,
    `end_time` timestamp not null,
    PRIMARY KEY (`id`),
    INDEX idx_show_movie (movie_id),
    INDEX idx_show_cinema_room (cinema_room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `seat_show`;
CREATE TABLE `seat_show` (
    `id` int NOT NULL AUTO_INCREMENT,
    `seat_id` int NOT NULL,
    `show_id` int NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    PRIMARY KEY (`id`),
    INDEX idx_seat_room (seat_id),
    INDEX idx_seat_show (show_id),
    INDEX idx_seat_status (status),
    CONSTRAINT fk_show_seat FOREIGN KEY (show_id) REFERENCES `show`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
