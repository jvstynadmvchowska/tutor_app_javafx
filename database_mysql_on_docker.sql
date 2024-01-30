CREATE DATABASE myapp1;
use myapp1;

CREATE TABLE IF NOT EXISTS `lessons` (
  `name` varchar(45) NOT NULL,
  `date` varchar(45) NOT NULL,
  `hours` double(3,2) DEFAULT NULL,
  `time` varchar(45) NOT NULL,
  `typeOfLesson` varchar(45) NOT NULL,
  PRIMARY KEY (`name`,`date`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `payments` (
  `name` varchar(45) NOT NULL,
  `date` varchar(45) NOT NULL,
  `amount` double(6,2) NOT NULL,
  PRIMARY KEY (`name`,`date`,`amount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `savings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quantity` double(6,2) NOT NULL,
  `date` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `students` (
  `name` varchar(45) NOT NULL,
  `rate` double(5,2) NOT NULL,
  `hoursWeekly` double(3,2) DEFAULT NULL,
  PRIMARY KEY (`name`,`rate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
