-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 18, 2022 at 02:02 AM
-- Server version: 10.4.22-MariaDB
-- PHP Version: 7.4.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `drinktracker`
--
CREATE DATABASE IF NOT EXISTS `drinktracker` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `drinktracker`;

-- --------------------------------------------------------

--
-- Table structure for table `deliveries`
--

CREATE TABLE `deliveries` (
  `delivery_id` int(6) NOT NULL,
  `delivery_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `store_id` int(4) DEFAULT NULL,
  `refurbished` decimal(7,1) DEFAULT NULL,
  `enroute` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `deliveries`
--

INSERT INTO `deliveries` (`delivery_id`, `delivery_date`, `store_id`, `refurbished`, `enroute`) VALUES
(1, '2022-04-02 22:25:31', 2, '9999.0', 0),
(3, '2022-04-16 13:44:09', 1, '500.0', 0),
(4, '2022-04-16 13:44:52', 4, '500.0', 0);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` int(6) NOT NULL,
  `delivery_id` int(11) DEFAULT 0,
  `product_id` int(12) DEFAULT 0,
  `order_qty` decimal(5,1) DEFAULT NULL,
  `order_date` timestamp NULL DEFAULT NULL,
  `order_completed` tinyint(1) DEFAULT NULL,
  `store_ordered` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `delivery_id`, `product_id`, `order_qty`, `order_date`, `order_completed`, `store_ordered`) VALUES
(1, 3, 100, '1000.0', '2022-04-16 14:46:30', 0, 'Quick Wok'),
(2, 0, 192, '9999.0', '2022-04-16 14:52:47', 1, 'Dirty Cup');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `product_id` int(3) NOT NULL,
  `product_name` varchar(50) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  `product_available` tinyint(1) DEFAULT NULL COMMENT '1=Y, 0=N',
  `product_moved` time DEFAULT NULL,
  `product_stock` decimal(5,1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`product_id`, `product_name`, `description`, `product_available`, `product_moved`, `product_stock`) VALUES
(100, 'Rockstar, Original', '16 fl oz can of Original Rockstar', 1, '11:26:10', '500.0'),
(192, 'Rockstar, Sugar Free', '16 fl oz can of Sugar Free Rockstar', 1, '11:26:12', '500.0'),
(576, 'Rockstar, Zero Carb', '16 fl oz can of Rockstar, Zero Excuses', 1, '11:26:15', '500.0'),
(705, 'Rockstar, Juiced Mango', '16 fl oz can of Mango Xdurance Rockstar', 1, '11:26:16', '500.0'),
(999, 'Testing', 'Testing purposes', 1, '19:32:52', '999.0');

-- --------------------------------------------------------

--
-- Table structure for table `stores`
--

CREATE TABLE `stores` (
  `store_id` int(3) NOT NULL,
  `store_name` varchar(50) NOT NULL,
  `last_updated` timestamp NULL DEFAULT NULL,
  `store_stock` decimal(5,1) DEFAULT NULL,
  `restocked` tinyint(1) DEFAULT NULL COMMENT '1=Y, 0=N'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `stores`
--

INSERT INTO `stores` (`store_id`, `store_name`, `last_updated`, `store_stock`, `restocked`) VALUES
(1, 'Dirty Cup', '2022-02-06 11:29:30', '100.0', 1),
(2, 'Quick Wok', '2022-02-06 11:33:25', '500.0', 0),
(3, 'A Latte Fun', '2022-02-06 11:37:03', '0.0', 0),
(4, 'Newer Egg', '2022-03-19 20:14:27', '5000.0', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `deliveries`
--
ALTER TABLE `deliveries`
  ADD PRIMARY KEY (`delivery_id`),
  ADD KEY `FK_deliveries_stores` (`store_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `FK_orders_products` (`product_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`);

--
-- Indexes for table `stores`
--
ALTER TABLE `stores`
  ADD PRIMARY KEY (`store_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `deliveries`
--
ALTER TABLE `deliveries`
  MODIFY `delivery_id` int(6) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int(6) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `deliveries`
--
ALTER TABLE `deliveries`
  ADD CONSTRAINT `FK_deliveries_stores` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `FK_orders_products` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;
  
GRANT ALL PRIVILEGES ON *.* TO `root`@`localhost` IDENTIFIED BY PASSWORD '*2470C0C06DEE42FD1618BB99005ADCA2EC9D1E19' WITH GRANT OPTION;

GRANT ALL PRIVILEGES ON `drinktracker`.* TO `root`@`localhost` WITH GRANT OPTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
