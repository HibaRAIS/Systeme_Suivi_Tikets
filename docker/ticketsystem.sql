CREATE DATABASE  IF NOT EXISTS `ticketsystem` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `ticketsystem`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: ticketsystem
-- ------------------------------------------------------
-- Server version	8.0.41

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

--
-- Table structure for table `emailnotifications`
--

DROP TABLE IF EXISTS `emailnotifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emailnotifications` (
                                      `NotificationID` int NOT NULL AUTO_INCREMENT,
                                      `UserID` int NOT NULL,
                                      `TicketID` int NOT NULL,
                                      `NotificationType` varchar(50) NOT NULL,
                                      `SentAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `DeliveryStatus` varchar(20) DEFAULT 'Pending',
                                      PRIMARY KEY (`NotificationID`),
                                      KEY `idx_email_notifications_user` (`UserID`),
                                      KEY `idx_email_notifications_ticket` (`TicketID`),
                                      CONSTRAINT `emailnotifications_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `users` (`user_id`),
                                      CONSTRAINT `emailnotifications_ibfk_2` FOREIGN KEY (`TicketID`) REFERENCES `tickets` (`TicketID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
                         `role_id` int NOT NULL,
                         `role_name` varchar(50) NOT NULL,
                         PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ticketcomments`
--

DROP TABLE IF EXISTS `ticketcomments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticketcomments` (
                                  `CommentID` int NOT NULL AUTO_INCREMENT,
                                  `TicketID` int NOT NULL,
                                  `UserID` int NOT NULL,
                                  `Comment` text NOT NULL,
                                  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`CommentID`),
                                  KEY `UserID` (`UserID`),
                                  KEY `idx_ticket_comments_ticket` (`TicketID`),
                                  CONSTRAINT `ticketcomments_ibfk_1` FOREIGN KEY (`TicketID`) REFERENCES `tickets` (`TicketID`),
                                  CONSTRAINT `ticketcomments_ibfk_2` FOREIGN KEY (`UserID`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ticketpriorities`
--

DROP TABLE IF EXISTS `ticketpriorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticketpriorities` (
                                    `PriorityID` int NOT NULL,
                                    `PriorityName` varchar(20) NOT NULL,
                                    PRIMARY KEY (`PriorityID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
                           `TicketID` int NOT NULL AUTO_INCREMENT,
                           `Title` varchar(100) NOT NULL,
                           `Description` text NOT NULL,
                           `CreatedByUserID` int NOT NULL,
                           `AssignedToUserID` int DEFAULT NULL,
                           `PriorityID` int NOT NULL,
                           `StatusID` int NOT NULL DEFAULT '1',
                           `TypeID` int NOT NULL,
                           `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `UpdatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           `ClosedAt` datetime DEFAULT NULL,
                           PRIMARY KEY (`TicketID`),
                           KEY `PriorityID` (`PriorityID`),
                           KEY `TypeID` (`TypeID`),
                           KEY `idx_tickets_created_by` (`CreatedByUserID`),
                           KEY `idx_tickets_assigned_to` (`AssignedToUserID`),
                           KEY `idx_tickets_status` (`StatusID`),
                           CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`CreatedByUserID`) REFERENCES `users` (`user_id`),
                           CONSTRAINT `tickets_ibfk_2` FOREIGN KEY (`AssignedToUserID`) REFERENCES `users` (`user_id`),
                           CONSTRAINT `tickets_ibfk_3` FOREIGN KEY (`PriorityID`) REFERENCES `ticketpriorities` (`PriorityID`),
                           CONSTRAINT `tickets_ibfk_4` FOREIGN KEY (`StatusID`) REFERENCES `ticketstatuses` (`StatusID`),
                           CONSTRAINT `tickets_ibfk_5` FOREIGN KEY (`TypeID`) REFERENCES `tickettypes` (`TypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ticketstatuses`
--

DROP TABLE IF EXISTS `ticketstatuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticketstatuses` (
                                  `StatusID` int NOT NULL,
                                  `StatusName` varchar(20) NOT NULL,
                                  PRIMARY KEY (`StatusID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ticketstatushistory`
--

DROP TABLE IF EXISTS `ticketstatushistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticketstatushistory` (
                                       `HistoryID` int NOT NULL AUTO_INCREMENT,
                                       `TicketID` int NOT NULL,
                                       `StatusID` int NOT NULL,
                                       `ChangedByUserID` int NOT NULL,
                                       `ChangedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`HistoryID`),
                                       KEY `StatusID` (`StatusID`),
                                       KEY `ChangedByUserID` (`ChangedByUserID`),
                                       KEY `idx_ticket_status_history_ticket` (`TicketID`),
                                       CONSTRAINT `ticketstatushistory_ibfk_1` FOREIGN KEY (`TicketID`) REFERENCES `tickets` (`TicketID`),
                                       CONSTRAINT `ticketstatushistory_ibfk_2` FOREIGN KEY (`StatusID`) REFERENCES `ticketstatuses` (`StatusID`),
                                       CONSTRAINT `ticketstatushistory_ibfk_3` FOREIGN KEY (`ChangedByUserID`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tickettypes`
--

DROP TABLE IF EXISTS `tickettypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickettypes` (
                               `TypeID` int NOT NULL,
                               `TypeName` varchar(50) NOT NULL,
                               PRIMARY KEY (`TypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
                         `user_id` int NOT NULL AUTO_INCREMENT,
                         `username` varchar(50) NOT NULL,
                         `email` varchar(100) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         `first_name` varchar(50) DEFAULT NULL,
                         `last_name` varchar(50) DEFAULT NULL,
                         `role_id` int NOT NULL,
                         PRIMARY KEY (`user_id`),
                         KEY `users_ibfk_1` (`role_id`),
                         CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'ticketsystem'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-28 17:08:44