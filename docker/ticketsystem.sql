CREATE DATABASE IF NOT EXISTS `ticketsystem` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `ticketsystem`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Table structure for table `roles`
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
                         `role_id` BIGINT NOT NULL AUTO_INCREMENT,
                         `role_name` VARCHAR(50) NOT NULL UNIQUE,
                         PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `roles` (`role_name`) VALUES
                                      ('ADMINISTRATOR'),
                                      ('TECHNICIAN'),
                                      ('USER');

-- Table structure for table `users`
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
                         `user_id` BIGINT NOT NULL AUTO_INCREMENT,         -- Changed to BIGINT
                         `username` VARCHAR(50) NOT NULL UNIQUE,
                         `email` VARCHAR(100) NOT NULL UNIQUE,
                         `password` VARCHAR(255) NOT NULL,
                         `first_name` VARCHAR(50) DEFAULT NULL,
                         `last_name` VARCHAR(50) DEFAULT NULL,
                         `role_id` BIGINT NOT NULL,                       -- Changed to BIGINT
                         PRIMARY KEY (`user_id`),
                         KEY `fk_users_role_id` (`role_id`),              -- Index for foreign key
                         CONSTRAINT `fk_users_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table structure for table `ticketpriorities`
DROP TABLE IF EXISTS `ticketpriorities`;
CREATE TABLE `ticketpriorities` (
                                    `PriorityID` INT NOT NULL,
                                    `PriorityName` VARCHAR(20) NOT NULL UNIQUE,
                                    PRIMARY KEY (`PriorityID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `ticketpriorities` (`PriorityID`, `PriorityName`) VALUES (1, 'Low'), (2, 'Medium'), (3, 'High');


-- Table structure for table `ticketstatuses`
DROP TABLE IF EXISTS `ticketstatuses`;
CREATE TABLE `ticketstatuses` (
                                  `StatusID` INT NOT NULL,
                                  `StatusName` VARCHAR(20) NOT NULL UNIQUE,
                                  PRIMARY KEY (`StatusID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `ticketstatuses` (`StatusID`, `StatusName`) VALUES (1, 'Open'), (2, 'In Progress'), (3, 'Resolved'), (4, 'Closed');


-- Table structure for table `tickettypes`
DROP TABLE IF EXISTS `tickettypes`;
CREATE TABLE `tickettypes` (
                               `TypeID` INT NOT NULL,
                               `TypeName` VARCHAR(50) NOT NULL UNIQUE,
                               PRIMARY KEY (`TypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `tickettypes` (`TypeID`, `TypeName`) VALUES (1, 'Incident'), (2, 'Request'), (3, 'Question');


-- Table structure for table `tickets`
DROP TABLE IF EXISTS `tickets`;
CREATE TABLE `tickets` (
                           `TicketID` BIGINT NOT NULL AUTO_INCREMENT,        -- Changed to BIGINT
                           `Title` VARCHAR(100) NOT NULL,
                           `Description` TEXT NOT NULL,
                           `CreatedByUserID` BIGINT NOT NULL,              -- Changed to BIGINT
                           `AssignedToUserID` BIGINT DEFAULT NULL,         -- Changed to BIGINT
                           `PriorityID` INT NOT NULL,
                           `StatusID` INT NOT NULL DEFAULT '1',
                           `TypeID` INT NOT NULL,
                           `CreatedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `UpdatedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           `ClosedAt` DATETIME DEFAULT NULL,
                           PRIMARY KEY (`TicketID`),
                           KEY `fk_tickets_CreatedByUserID` (`CreatedByUserID`),
                           KEY `fk_tickets_AssignedToUserID` (`AssignedToUserID`),
                           KEY `fk_tickets_PriorityID` (`PriorityID`),
                           KEY `fk_tickets_StatusID` (`StatusID`),
                           KEY `fk_tickets_TypeID` (`TypeID`),
                           CONSTRAINT `fk_tickets_CreatedByUserID` FOREIGN KEY (`CreatedByUserID`) REFERENCES `users` (`user_id`),
                           CONSTRAINT `fk_tickets_AssignedToUserID` FOREIGN KEY (`AssignedToUserID`) REFERENCES `users` (`user_id`),
                           CONSTRAINT `fk_tickets_PriorityID` FOREIGN KEY (`PriorityID`) REFERENCES `ticketpriorities` (`PriorityID`),
                           CONSTRAINT `fk_tickets_StatusID` FOREIGN KEY (`StatusID`) REFERENCES `ticketstatuses` (`StatusID`),
                           CONSTRAINT `fk_tickets_TypeID` FOREIGN KEY (`TypeID`) REFERENCES `tickettypes` (`TypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table structure for table `ticketcomments`
DROP TABLE IF EXISTS `ticketcomments`;
CREATE TABLE `ticketcomments` (
                                  `CommentID` BIGINT NOT NULL AUTO_INCREMENT,       -- Changed to BIGINT
                                  `TicketID` BIGINT NOT NULL,                     -- Changed to BIGINT
                                  `UserID` BIGINT NOT NULL,                       -- Changed to BIGINT
                                  `Comment` TEXT NOT NULL,
                                  `CreatedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`CommentID`),
                                  KEY `fk_ticketcomments_TicketID` (`TicketID`),
                                  KEY `fk_ticketcomments_UserID` (`UserID`),
                                  CONSTRAINT `fk_ticketcomments_TicketID` FOREIGN KEY (`TicketID`) REFERENCES `tickets` (`TicketID`) ON DELETE CASCADE,
                                  CONSTRAINT `fk_ticketcomments_UserID` FOREIGN KEY (`UserID`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table structure for table `ticketstatushistory`
DROP TABLE IF EXISTS `ticketstatushistory`;
CREATE TABLE `ticketstatushistory` (
                                       `HistoryID` BIGINT NOT NULL AUTO_INCREMENT,     -- Changed to BIGINT
                                       `TicketID` BIGINT NOT NULL,                   -- Changed to BIGINT
                                       `StatusID` INT NOT NULL,
                                       `ChangedByUserID` BIGINT NOT NULL,            -- Changed to BIGINT
                                       `ChangedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`HistoryID`),
                                       KEY `fk_ticketstatushistory_TicketID` (`TicketID`),
                                       KEY `fk_ticketstatushistory_StatusID` (`StatusID`),
                                       KEY `fk_ticketstatushistory_ChangedByUserID` (`ChangedByUserID`),
                                       CONSTRAINT `fk_ticketstatushistory_TicketID` FOREIGN KEY (`TicketID`) REFERENCES `tickets` (`TicketID`) ON DELETE CASCADE,
                                       CONSTRAINT `fk_ticketstatushistory_StatusID` FOREIGN KEY (`StatusID`) REFERENCES `ticketstatuses` (`StatusID`),
                                       CONSTRAINT `fk_ticketstatushistory_ChangedByUserID` FOREIGN KEY (`ChangedByUserID`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table structure for table `emailnotifications`
DROP TABLE IF EXISTS `emailnotifications`;
CREATE TABLE `emailnotifications` (
                                      `NotificationID` BIGINT NOT NULL AUTO_INCREMENT, -- Changed to BIGINT
                                      `UserID` BIGINT NOT NULL,                         -- Changed to BIGINT
                                      `TicketID` BIGINT NOT NULL,                       -- Changed to BIGINT
                                      `NotificationType` VARCHAR(50) NOT NULL,
                                      `SentAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `DeliveryStatus` VARCHAR(20) DEFAULT 'Pending',
                                      PRIMARY KEY (`NotificationID`),
                                      KEY `fk_emailnotifications_UserID` (`UserID`),
                                      KEY `fk_emailnotifications_TicketID` (`TicketID`),
                                      CONSTRAINT `fk_emailnotifications_UserID` FOREIGN KEY (`UserID`) REFERENCES `users` (`user_id`),
                                      CONSTRAINT `fk_emailnotifications_TicketID` FOREIGN KEY (`TicketID`) REFERENCES `tickets` (`TicketID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;