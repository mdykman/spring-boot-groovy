SET FOREIGN_KEY_CHECKS=0;
drop table if exists product_category;
create table product_category (
	id int NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'unique ID for each category entry',
	name varchar(50) NOT NULL
);


drop table if exists product;
create table product (
	`id` int PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT 'unique ID for each product entry',
	`name` varchar(50) COMMENT 'Name of product',
	`description` varchar(255),
	`date_added` timestamp default current_timestamp,
	`brand` varchar(50) NOT NULL,
	`series` varchar(25),
	`product_number` varchar(50),
	`sku` varchar(100),
	`upc` varchar(100),
	`ean` varchar (100),
	`category` int NOT NULL,
	`status` varchar(50) NOT NULL default 'Active' COMMENT 'Can be Active, Discontinued, Deleted, or Preview',
	FOREIGN KEY (category) REFERENCES product_category(id) ON DELETE CASCADE
);

drop table if exists product_attribute;
create table product_attribute (
	`id` int PRIMARY KEY AUTO_INCREMENT NOT NULL,
	`product_id` int NOT NULL,
	`key` varchar(50) NOT NULL,
	`name` varchar(50) NOT NULL,
	`description` varchar(100) NOT NULL,
	`value` varchar(100),
	`unit` varchar(25),
	FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

drop table if exists product_options;
create table product_options (
	`id` int PRIMARY KEY AUTO_INCREMENT NOT NULL,
	`product_id` int NOT NULL,
	`key` varchar(50) NOT NULL,
	`name` varchar(50) NOT NULL,
	`description` varchar(100) NOT NULL,
	`value` varchar(100),
	`unit` varchar(25),
	FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

/*
drop table if exists product_value_override;
create table product_value_override (
	`id` int PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT 'unique ID for each product_value_override entry',
	`firm_id` int(11) NOT NULL,
	`product_id` int NOT NULL,
	`attribute` tinyint(1) default '1',
	`key` varchar(50) NOT NULL,
	`value` varchar(100),
	`unit` varchar(25),
	FOREIGN KEY (firm_id) REFERENCES firm(id) ON DELETE CASCADE,
	FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);
*/
SET FOREIGN_KEY_CHECKS=1;
