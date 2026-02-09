-- MySQL dump 10.13  Distrib 9.4.0, for Win64 (x86_64)
--
-- Host: localhost    Database: bank_db
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP DATABASE IF EXISTS `bank_db`;
CREATE DATABASE `bank_db` DEFAULT CHARACTER SET utf8;
USE `bank_db`;

--
-- Table structure for table `access_log`
--

DROP TABLE IF EXISTS `access_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `access_log` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `access_date` datetime DEFAULT NULL,
  `success_or_not` tinyint(1) DEFAULT NULL,
  `user_id` varchar(50) DEFAULT NULL,
  `admin_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`log_id`),
  KEY `user_id` (`user_id`),
  KEY `admin_id` (`admin_id`),
  CONSTRAINT `access_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `access_log_ibfk_2` FOREIGN KEY (`admin_id`) REFERENCES `administrator` (`admin_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access_log`
--

LOCK TABLES `access_log` WRITE;
/*!40000 ALTER TABLE `access_log` DISABLE KEYS */;
INSERT INTO `access_log` VALUES (1,'2025-12-07 16:31:07',1,'1',NULL),(2,'2025-12-07 17:03:50',0,'1',NULL),(4,'2025-12-07 17:22:06',0,'1',NULL),(5,'2025-12-07 17:22:10',1,'1',NULL),(6,'2025-12-07 18:09:25',1,'1',NULL),(7,'2025-12-08 02:00:37',0,'1',NULL),(8,'2025-12-08 02:00:44',1,'1',NULL),(9,'2025-12-08 02:10:57',1,'1',NULL),(10,'2025-12-08 02:37:23',1,'1',NULL),(11,'2025-12-08 03:28:32',1,'1',NULL),(12,'2025-12-08 03:44:51',1,NULL,NULL),(13,'2025-12-08 03:45:44',1,'1',NULL),(14,'2025-12-08 03:47:32',1,NULL,NULL),(15,'2025-12-08 04:14:28',1,NULL,'admin'),(16,'2025-12-08 07:23:18',1,NULL,'admin'),(17,'2025-12-08 07:51:56',1,'1',NULL),(18,'2025-12-08 07:53:01',1,NULL,NULL),(19,'2025-12-08 07:55:55',1,'1',NULL),(20,'2025-12-08 07:57:15',1,NULL,'admin'),(21,'2025-12-08 08:45:54',1,'1',NULL),(22,'2025-12-08 08:52:55',1,NULL,'admin'),(23,'2025-12-08 08:53:22',1,'1',NULL),(24,'2025-12-08 09:05:04',1,NULL,'admin'),(25,'2025-12-08 09:06:16',1,'1',NULL),(26,'2025-12-08 09:24:54',1,NULL,'admin'),(27,'2025-12-08 09:57:03',1,'1',NULL),(28,'2025-12-08 10:05:42',1,NULL,'admin'),(29,'2025-12-08 10:08:41',1,NULL,'admin'),(32,'2025-12-08 15:33:51',1,'1',NULL),(34,'2025-12-08 15:34:18',0,'1',NULL),(37,'2025-12-08 16:46:03',1,'1',NULL),(38,'2025-12-08 17:02:55',1,'3',NULL),(39,'2025-12-08 17:41:43',1,'1',NULL),(40,'2025-12-08 18:29:51',1,NULL,'admin'),(41,'2025-12-09 02:32:17',1,NULL,'admin'),(42,'2025-12-09 02:33:17',1,NULL,NULL),(43,'2025-12-09 02:49:19',1,NULL,'admin'),(44,'2025-12-09 15:12:40',1,'1',NULL),(45,'2025-12-09 15:12:51',1,NULL,'admin'),(46,'2025-12-09 15:13:12',0,NULL,'admin'),(47,'2025-12-09 15:13:16',1,NULL,'admin'),(48,'2025-12-09 19:30:01',1,NULL,'admin'),(49,'2025-12-09 19:37:10',1,NULL,'admin'),(50,'2025-12-09 19:39:28',1,'1',NULL),(51,'2025-12-09 19:48:23',1,'1',NULL);
/*!40000 ALTER TABLE `access_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `account_id` int NOT NULL,
  `account_status` varchar(20) DEFAULT NULL,
  `balance` bigint DEFAULT '0',
  `transfer_limit` bigint DEFAULT NULL,
  `opening_date` datetime DEFAULT NULL,
  `owner_id` varchar(50) DEFAULT NULL,
  `account_password` varchar(4) DEFAULT '0000',
  `wrong_pw_count` int DEFAULT '0',
  PRIMARY KEY (`account_id`),
  KEY `owner_id` (`owner_id`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'DORMANT',80000,10000,'2025-12-08 18:18:18','1','0000',1),(2115,'LOCKED',5000,1000000,'2025-12-09 19:40:25','1','1234',3),(967361,'ACTIVE',63900,1000000,'2025-12-07 18:09:32','1','0000',0),(5296492,'CLOSED',3000,100000,'2025-12-08 02:00:54','1','1111',0),(6523996,'ACTIVE',41000,1000000,'2025-12-08 02:11:20','1','1234',0);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `administrator`
--

DROP TABLE IF EXISTS `administrator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrator` (
  `admin_id` varchar(50) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `ssn` varchar(20) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrator`
--

LOCK TABLES `administrator` WRITE;
/*!40000 ALTER TABLE `administrator` DISABLE KEYS */;
INSERT INTO `administrator` VALUES ('admin','010-0000-0000','Super','Admin','Bank','999999-1111111','1234');
/*!40000 ALTER TABLE `administrator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `transaction_type` varchar(20) DEFAULT NULL,
  `amount` bigint DEFAULT NULL,
  `transaction_time` datetime DEFAULT NULL,
  `depos_id` int DEFAULT NULL,
  `withd_id` int DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `transaction_ibfk_1` (`depos_id`),
  KEY `transaction_ibfk_2` (`withd_id`),
  CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`depos_id`) REFERENCES `account` (`account_id`) ON DELETE SET NULL,
  CONSTRAINT `transaction_ibfk_2` FOREIGN KEY (`withd_id`) REFERENCES `account` (`account_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (1,'DEPOSIT',100000,'2025-12-08 02:38:14',6523996,NULL),(2,'WITHDRAW',5000,'2025-12-08 03:29:36',NULL,6523996),(3,'DEPOSIT',10000,'2025-12-08 03:45:36',NULL,NULL),(4,'TRANSFER',5000,'2025-12-08 03:46:22',NULL,6523996),(5,'DEPOSIT',15000,'2025-12-08 07:53:57',967361,NULL),(6,'WITHDRAW',5000,'2025-12-08 08:46:27',NULL,967361),(7,'WITHDRAW',100,'2025-12-08 08:47:32',NULL,967361),(8,'TRANSFER',1000,'2025-12-08 09:06:55',6523996,967361),(9,'TRANSFER',50000,'2025-12-08 09:09:03',967361,6523996),(10,'WITHDRAW',10000,'2025-12-08 09:22:08',NULL,1),(11,'TRANSFER',10000,'2025-12-08 10:01:08',5296492,1),(12,'WITHDRAW',7000,'2025-12-08 10:01:55',NULL,5296492),(13,'DEPOSIT',10000,'2025-12-09 19:41:57',2115,NULL),(14,'TRANSFER',5000,'2025-12-09 19:43:35',967361,2115);
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` varchar(50) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `ssn` varchar(20) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `signup_date` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('1','1','1','1','1','1','1','2025-12-07 16:06:55'),('3','333','3','33','3333','33333','3333','2025-12-08 17:01:35');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-09 20:14:38
