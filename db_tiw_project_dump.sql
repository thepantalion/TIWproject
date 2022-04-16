-- MySQL dump 10.13  Distrib 8.0.28, for macos11 (x86_64)
--
-- Host: 127.0.0.1    Database: db_tiw_project
-- ------------------------------------------------------
-- Server version	8.0.28

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
-- Table structure for table `invited`
--

DROP TABLE IF EXISTS `invited`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invited` (
  `idUser` int NOT NULL,
  `idMeeting` int NOT NULL,
  PRIMARY KEY (`idUser`,`idMeeting`),
  KEY `idMeeting_idx` (`idMeeting`),
  CONSTRAINT `idMeeting` FOREIGN KEY (`idMeeting`) REFERENCES `meeting` (`idmeeting`) ON UPDATE CASCADE,
  CONSTRAINT `idUser` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invited`
--

LOCK TABLES `invited` WRITE;
/*!40000 ALTER TABLE `invited` DISABLE KEYS */;
INSERT INTO `invited` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(1,2),(2,2),(8,2),(1,3),(2,3),(1,4),(2,4),(5,4),(6,4),(1,5),(2,5),(5,5),(6,5);
/*!40000 ALTER TABLE `invited` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meeting`
--

DROP TABLE IF EXISTS `meeting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `meeting` (
  `idmeeting` int NOT NULL AUTO_INCREMENT,
  `title` varchar(45) NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `duration` int NOT NULL,
  `maxNumOfParticipants` int NOT NULL,
  `idCreator` int NOT NULL,
  PRIMARY KEY (`idmeeting`),
  KEY `idCreator_idx` (`idCreator`),
  CONSTRAINT `idCreator` FOREIGN KEY (`idCreator`) REFERENCES `user` (`idUser`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meeting`
--

LOCK TABLES `meeting` WRITE;
/*!40000 ALTER TABLE `meeting` DISABLE KEYS */;
INSERT INTO `meeting` VALUES (1,'I disagiati del Poli','2022-04-16','15:30:00',90,7,2),(2,'Prog. IngSW - 2k21','2021-06-22','00:30:00',270,3,1),(3,'TIW','2022-04-18','11:00:00',60,2,2),(4,'Economia & Co.','2022-04-28','14:30:00',120,4,6),(5,'Automatica $ Co.','2022-01-10','13:00:00',360,3,1);
/*!40000 ALTER TABLE `meeting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `idUser` int NOT NULL AUTO_INCREMENT,
  `username` varchar(30) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`idUser`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'thepantalion','filippo.pantaleone@mail.polimi.com','08032000'),(2,'chiaraperuzzi','chiara.peruzzi@mail.polimi.it','07051999'),(3,'ellie_spoken','elisa.parlati@mail.polimi.it','melecutugno99'),(4,'gjus3ppe','giuseppe.marchesani@mail.polimi.it','Giuseppe99'),(5,'luca.nicoli99','luca.nicoli@mail.polimi.it','@tato99'),(6,'michelangelo_mascari','michelangelo.mascari@mail.polimi.it','MostroDiFirenze'),(7,'fedeorla99','federico.orlando@unimi.it','traditore99'),(8,'naldi.valentina','valentina.naldi@mail.polimi.it','melecutugno99');
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

-- Dump completed on 2022-04-16 16:43:02
