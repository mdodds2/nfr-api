DROP TABLE category;
CREATE TABLE `nfr`.`category` (
  `id` BINARY(16) NOT NULL DEFAULT (UUID_TO_BIN(UUID())),
  `ordinal` INTEGER NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `short_name` VARCHAR(10) NOT NULL,
  `description` VARCHAR(500) NOT NULL,
  `sort_order` INTEGER NOT NULL,
  `is_active` TINYINT NOT NULL DEFAULT 1,
  `image` VARCHAR(100) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE (`name`),
  UNIQUE (`short_name`)
);

insert into category (ordinal, name, short_name, description, sort_order) values (1,'Functional Suitability', 'FSU', 'This characteristic represents the degree to which a product or system provides functions that meet stated and implied needs when used under specified conditions.', 1);
insert into category (ordinal, name, short_name, description, sort_order) values (2,'Performance Efficiency', 'PEF', 'This characteristic represents the degree to which a product performs its functions within specified time and throughput parameters and is efficient in the use of resources (such as CPU, memory, storage, network devices, energy, materials...) under specified conditions.', 2);
insert into category (ordinal, name, short_name, description, sort_order) values (3,'Compatibility', 'COM', 'Degree to which a product, system or component can exchange information with other products, systems or components, and/or perform its required functions while sharing the same common environment and resources.', 3);
insert into category (ordinal, name, short_name, description, sort_order) values (4,'Interaction Capability', 'INC', 'Degree to which a product or system can be interacted with by specified users to exchange information ia the user interface to complete specific tasks in a variety of contexts of use.', 4);
insert into category (ordinal, name, short_name, description, sort_order) values (5,'Reliability', 'REL', 'Degree to which a system, product or component performs specified functions under specified conditions for a specified period of time.', 5);
insert into category (ordinal, name, short_name, description, sort_order) values (6,'Security', 'SEC', 'Degree to which a product or system defends against attack patterns by malicious actos and protects information and data so that persons or other products or systems have the degree of data access appropriate to their types and levels of authorization.', 6);
insert into category (ordinal, name, short_name, description, sort_order) values (7,'Maintainability', 'MAI', 'This characteristic represents the degree of effectiveness and efficiency with which a product or system can be modified to improve it, correct it or adapt it to changes in environment, and in requirements.', 7);
insert into category (ordinal, name, short_name, description, sort_order) values (8,'Flexibility', 'FLX', 'Degree to which a product can be adapted to changes in its requirements, contexts of use or sys tem environment.', 8);
insert into category (ordinal, name, short_name, description, sort_order) values (9,'Safety', 'SAF', 'This characteristic represents the degree to which a product under defined conditions to avoid a state in which human life, health, property, or the environment is endangered.', 9);

