-- MySQL dump 10.9
--
-- Host: localhost    Database: seamdemo
-- ------------------------------------------------------
-- Server version	4.1.14-standard

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Booking`
--

DROP TABLE IF EXISTS `Booking`;
CREATE TABLE `Booking` (
  `id` bigint(20) NOT NULL auto_increment,
  `creditCard` varchar(16) NOT NULL default '',
  `checkinDate` date NOT NULL default '0000-00-00',
  `checkoutDate` date NOT NULL default '0000-00-00',
  `user_username` varchar(255) default NULL,
  `hotel_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK6713A0396E4A3BD` (`user_username`),
  KEY `FK6713A03951897512` (`hotel_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Booking`
--


--
-- Table structure for table `Hotel`
--

DROP TABLE IF EXISTS `Hotel`;
CREATE TABLE `Hotel` (
  `id` bigint(20) NOT NULL auto_increment,
  `address` varchar(100) NOT NULL default '',
  `name` varchar(50) NOT NULL default '',
  `state` char(2) NOT NULL default '',
  `city` varchar(20) NOT NULL default '',
  `zip` varchar(5) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Hotel`
--


/*!40000 ALTER TABLE `Hotel` DISABLE KEYS */;
LOCK TABLES `Hotel` WRITE;
INSERT INTO `Hotel` VALUES (1,'Tower Place, Buckhead','Marriott Courtyard','GA','Atlanta','30305'),(2,'Tower Place, Buckhead','Doubletree','GA','Atlanta','30305'),(3,'Union Square, Manhattan','W Hotel','NY','NY','10011'),(4,'Lexington Ave, Manhattan','W Hotel','NY','NY','10011'),(5,'Dupont Circle','Hotel Rouge','DC','Washington','20036'),(6,'70 Park Avenue','70 Park Avenue Hotel','NY','NY','10011'),(8,'1395 Brickell Ave','Conrad Miami','FL','Miami','33131'),(9,'2106 N Clairemont Ave','Sea Horse Inn','WI','Eau Claire','54703'),(10,'1151 W Macarthur Ave','Super 8 Eau Claire Campus Area','WI','Eau Claire','54701'),(11,'55 Fourth Street','Marriot Downtown','CA','San Francisco','94103');
UNLOCK TABLES;
/*!40000 ALTER TABLE `Hotel` ENABLE KEYS */;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
  `username` varchar(255) NOT NULL default '',
  `name` varchar(100) NOT NULL default '',
  `password` varchar(15) NOT NULL default '',
  PRIMARY KEY  (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `User`
--


/*!40000 ALTER TABLE `User` DISABLE KEYS */;
LOCK TABLES `User` WRITE;
INSERT INTO `User` VALUES ('gavin','Gavin King','foobar'),('demo','Demo User','demo');
UNLOCK TABLES;
/*!40000 ALTER TABLE `User` ENABLE KEYS */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

