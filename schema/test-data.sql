-- MySQL dump 10.13  Distrib 5.7.20, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: ats_rules
-- ------------------------------------------------------
-- Server version	5.7.20-0ubuntu0.16.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Faucet'),(2,'Basin'),(3,'Sink'),(4,'Cable'),(5,'Carrier'),(6,'Drainage'),(7,'Trap');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `category_attributes`
--

LOCK TABLES `category_attributes` WRITE;
/*!40000 ALTER TABLE `category_attributes` DISABLE KEYS */;
INSERT INTO `category_attributes` VALUES (1,1,'powered',1,'a'),(2,1,'power-voltage',1,'a'),(3,1,'power-ampheres',1,'a'),(4,1,'power-watts',1,'a'),(5,1,'power_type',1,'a'),(6,1,'connection_type',1,'a'),(7,1,'motion_sensor',1,'a'),(8,1,'num_intake',1,'a'),(9,1,'pipe_gauge',1,'a'),(10,1,'pipe_on_center',1,'a'),(11,1,'pipe_diameter',1,'a'),(12,3,'num_faucet_holes',1,'a'),(13,3,'Faucet_hole_Diameter',1,'a'),(14,3,'Faucet_Hole_on_center',1,'a'),(15,3,'Drainage_Size',1,'a'),(16,3,'mounting_configuration',1,'a'),(17,4,'Voltage',1,'a'),(18,4,'Ampheres',1,'a'),(19,4,'wattage',1,'a'),(20,4,'connection',1,'a'),(21,5,'weightLimit',1,'a'),(22,5,'moutingBraket',1,'a'),(23,5,'NegativePower',1,'a'),(24,6,'Flange_Size',1,'a');
/*!40000 ALTER TABLE `category_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `rule`
--

LOCK TABLES `rule` WRITE;
/*!40000 ALTER TABLE `rule` DISABLE KEYS */;
INSERT INTO `rule` VALUES (9,'ats1','6e8dc66118fd3c300d1c4477b952aac3','if(sink) switch(faucet.pipes)\n    case 1: sink.holes = 1 or sink.holes = 3\n    case 2: sink.holes = 2\n    case 3: sink.holes = 3','Match sink holes',NULL,1.00,'987150e0-ef28-11e7-922b-201a06da5a96'),(10,'ats1','e4fa7b0016d851018d6003974d44347a','switch(faucet.pipes)\n       case 1: faucet.pipediameter < sink.holediameter OR faucet.pipeguage < sink.centreholediameter\n       case 2:\n       case 3:','Fit sink holes',NULL,1.00,'98715850-ef28-11e7-922b-201a06da5a96'),(11,'ats1','d5ebb646b8b208f77563a1f760c25eee','faucet.pipeoncentre = sink.holeoncentre','Match faucet pipe centre to sink pipes',NULL,1.00,'98715cb1-ef28-11e7-922b-201a06da5a96'),(12,'ats1','a1396b5c6a015047f4539d2d522e077e','((sink.holediameter - faucet.pipeguage) / 2) < abs(sink.holedistance-faucet.pipedistance)','Match fauct pipe diameter to sink pipes',NULL,1.00,'98716147-ef28-11e7-922b-201a06da5a96'),(13,'ats1','d0a61283e771d6380ea395a1f05d9a28','if(faucet.connection_type) {\n musthave (powercable)\n}','power faucet must have power cable',NULL,1.00,'98716616-ef28-11e7-922b-201a06da5a96'),(14,'ats1','9a054b26e93dcbe886af5f766af55184','if(faucet.connection_type) {\n connectors(faucet.connection_type, powercable.connection) // connector wrong\n powercable.voltage = faucet.voltage\n powercable.amprs >= faucet.amps\n powercable.watts >= faucet.watts\n}','power faucet connectors must match voltage and meet minimum amps',NULL,1.00,'98716b3b-ef28-11e7-922b-201a06da5a96'),(15,'ats1','7a54a31513a625895a86f933dd556b02','if(carrier.negative_power) {   ! faucet.connection_type }','Match fauct pipe diameter to sink pipes',NULL,1.00,'9882b19d-ef28-11e7-922b-201a06da5a96'),(16,'ats1','82143c8d0db77530b578e8ffa3594b65','sink.drainagesize == drainage.flangesize (number + unit)','Match fauct pipe diameter to sink pipes',NULL,1.00,'9882b63b-ef28-11e7-922b-201a06da5a96'),(17,'ats2','10c2c2faa56dd53434fd5645b6f1698b','faucet.powered ? cable.voltage == faucet[\'power-voltage\']  : true','if a faucet is powered, it must have a cable',NULL,1.00,'9882ba3b-ef28-11e7-922b-201a06da5a96'),(19,'ats2','8e4a82deccac93b045c00b2e49c7f6da','faucet.powered ? ((cable.voltage == faucet[\'power-voltage\']) & (cable.ampheres >= faucet[\'power-ampheres\'])) : true','if a faucet is powered, it must have a suitable cable',NULL,1.00,'9882bdfc-ef28-11e7-922b-201a06da5a96'),(21,'ats2','ba34a787761d78eeebdd8ae938227449','require(sink)','requires a sink',NULL,1.00,'9882c1ae-ef28-11e7-922b-201a06da5a96'),(23,'ats3','004a15b40f0be9cd119138ec623d8fdd','carrier.product_id || true','test',NULL,1.00,'9882c61a-ef28-11e7-922b-201a06da5a96'),(24,'ats2','fa216836f43e89c6f51acf1d42942c5e','faucet.num_intake == sink.num_faucet_holes || ((faucet.num_intake == 1) & (sink.num_faucet_holes == 3)) ','does the sink fit the  faucet',NULL,1.00,'9882c9e0-ef28-11e7-922b-201a06da5a96'),(26,'ats2','c66e99b6b2ec0bd48e3e90b8994cf80e','require(faucet)','required a faucet',NULL,1.00,'9882cd97-ef28-11e7-922b-201a06da5a96'),(27,'ats2','4f364b1bd7656adee93b0f4b14ac03a9','require(drainage)','a sick must have drainage',NULL,1.00,'9882d12c-ef28-11e7-922b-201a06da5a96'),(28,'ats2','a557cca33a4b66279266f19dd55a84bf','sink.drainage_size == drainage.flange_size','the flage must fit the sink',NULL,1.00,'9882d4c4-ef28-11e7-922b-201a06da5a96'),(29,'error1','7df86c99a4f4bb31b66c9e54fb8059aa','require(notexist)','we need a category that does not exist',NULL,1.00,'9882d868-ef28-11e7-922b-201a06da5a96'),(30,'error2','5b3346b0be7188227ec12ace3100a250','sink.unknown_attribute = 42','we need a category that does not exist',NULL,1.00,'9882dc32-ef28-11e7-922b-201a06da5a96'),(31,'error3','3fa318678de919189b4e33cb222e0052','require(trap)','a faucet requires a trap',NULL,1.00,'9882dfd8-ef28-11e7-922b-201a06da5a96');
/*!40000 ALTER TABLE `rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `rule_map`
--

LOCK TABLES `rule_map` WRITE;
/*!40000 ALTER TABLE `rule_map` DISABLE KEYS */;
INSERT INTO `rule_map` VALUES (1,9,1),(2,9,3),(3,10,1),(4,10,3),(5,11,1),(6,11,3),(7,12,1),(8,12,3),(9,13,1),(10,13,3),(11,14,1),(12,15,5),(13,16,3),(14,16,6),(15,17,1),(16,19,1),(17,21,1),(18,23,1),(19,24,1),(20,26,3),(21,27,3),(22,28,3),(23,28,6),(24,29,1),(25,30,3),(26,31,1);
/*!40000 ALTER TABLE `rule_map` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-01-02 22:52:18
