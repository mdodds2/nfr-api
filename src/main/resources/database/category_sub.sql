DROP TABLE category_sub;
CREATE TABLE `nfr`.`category_sub` (
  `id` BINARY(16) NOT NULL DEFAULT (UUID_TO_BIN(UUID())),
  `category_id` BINARY(16) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `short_name` VARCHAR(10) NOT NULL,
  `description` VARCHAR(500) NOT NULL,
  `sort_order` INT NOT NULL,
  `is_active` TINYINT NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE (`name`),
  UNIQUE (`short_name`)
);

ALTER TABLE category_sub ADD CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES category(id);

-- Functional Suitability
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=1), 'Functional Completeness', 'FCO', 'Degree to which the set of functions covers all the specified tasks and intended users'' objectives.', 1);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=1), 'Functional Correctness', 'FCR', 'Degree to which a product or system provides accurate results when used by intended users.', 2);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=1), 'Functional Appropriateness', 'FAP', 'Degree to which the functions facilitate the accomplishment of specified tasks and objectives.', 3);

-- Performance Efficiency
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=2), 'Time Behaviour', 'TIM', 'Degree to which the response time and throughput rates of a product or system, when performing its functions, meet requirements.', 100);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=2), 'Resource Utilization', 'RUT', 'Degree to which the amounts and types of resources used by a product or system, when performing its functions, meet requirements.', 101);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=2), 'Capacity ', 'CAP', 'Degree to which the maximum limits of a product or system parameter meet requirements.', 102);

-- Compatibility
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=3), 'Co-existence', 'COE', 'Degree to which a product can perform its required functions efficiently while sharing a common environment and resources with other products, without detrimental impact on any other product.', 200);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=3), 'Interoperability ', 'INT', 'Degree to which a system, product or component can exchange information with other products and mutually use the information that has been exchanged.', 201);

-- Interaction Capability
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=4), 'Appropriateness Recognizability', 'ARE', 'Degree to which users can recognize whether a product or system is appropriate for their needs.', 300);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=4), 'Learnability', 'LEA', 'Degree to which the functions of a product or system can be learnt to be used by specified users within a specified amount of time.', 301);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=4), 'Operability', 'OPR', 'Degree to which a product or system has attributes that make it easy to operate and control.', 302);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=4), 'User Error Protection', 'UEP', 'Degree to which a system prevents users against operation errors.', 303);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=4), 'User Engagement', 'UEN', 'Degree to which a user interface presents functions and information in an inviting and motivating manner encouraging continued interaction.', 304);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=4), 'Inclusivity', 'INC', 'Degree to which a product or system can be used by people of various backgrounds (such as people of various ages, abilities, cultures, ethnicities, languages, genders, economic situations, etc.).', 305);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=4), 'User Assistance', 'UAS', 'Degree to which a product can be used by people with the widest range of characteristics and capabilities to achieve specified goals in a specified context of use.', 306);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=4), 'Self-descriptiveness', 'SDE', 'Degree to which a product presents appropriate information, where needed by the user, to make its capabilities and use immediately obvious to the user without excessive interactions with a product or other resources (such as user documentation, help desks or other users).', 307);

-- Reliability
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=5), 'Faultlessness', 'FAU', 'Degree to which a system, product or component performs specified functions without fault under normal operation.', 400);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=5), 'Availability', 'AVA', 'Degree to which a system, product or component is operational and accessible when required for use.', 401);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=5), 'Fault Tolerance', 'FTO', 'Degree to which a system, product or component operates as intended despite the presence of hardware or software faults.', 402);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=5), 'Recoverability', 'REC', 'Degree to which, in the event of an interruption or a failure, a product or system can recover the data directly affected and re-establish the desired state of the system.', 403);

-- Security
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=6), 'Confidentiality', 'CON', 'Degree to which a product or system ensures that data are accessible only to those authorized to have access.', 500);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=6), 'Integrity', 'ING', 'Degree to which a system, product or component ensures that the state of its system and data are protected from unauthorized modification or deletion either by malicious action or computer error.', 501);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=6), 'Non-repudiation', 'NRP', 'Degree to which actions or events can be proven to have taken place so that the events or actions cannot be repudiated later.', 502);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=6), 'Accountability', 'ACT', 'Degree to which the actions of an entity can be traced uniquely to the entity.', 503);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=6), 'Authenticity', 'AUT', 'Degree to which the identity of a subject or resource can be proved to be the one claimed.', 504);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=6), 'Resistance', 'RES', 'Degree to which the product or system sustains operations while under attack from a malicious actor.', 505);

-- Maintainability
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=7), 'Modularity', 'MOD', 'Degree to which a system or computer program is composed of discrete components such that a change to one component has minimal impact on other components.', 600);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=7), 'Reusability', 'REU', 'Degree to which a product can be used as an asset in more than one system, or in building other assets.', 601);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=7), 'Analyzability', 'ANZ', 'Degree of effectiveness and efficiency with which it is possible to assess the impact on a product or system of an intended change to one or more of its parts, to diagnose a product for deficiencies or causes of failures, or to identify parts to be modified.', 602);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=7), 'Modifiability', 'MDF', 'Degree to which a product or system can be effectively and efficiently modified without introducing defects or degrading existing product quality.', 603);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=7), 'Testability', 'TST', 'Degree of effectiveness and efficiency with which test criteria can be established for a system, product or component and tests can be performed to determine whether those criteria have been met.', 604);

-- Flexibility
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=8), 'Adaptability', 'ADP', 'Degree to which a product or system can effectively and efficiently be adapted for or transferred to different hardware, software or other operational or usage environments.', 700);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=8), 'Scalability', 'SCA', 'Degree to which a product can handle growing or shrinking workloads or to adapt its capacity to handle variability.', 701);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=8), 'Installability', 'INS', 'Degree of effectiveness and efficiency with which a product or system can be successfully installed and/or uninstalled in a specified environment.', 702);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=8), 'Replaceability', 'REP', 'Degree to which a product can replace another specified software product for the same purpose in the same environment.', 703);

-- Safety
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=9), 'Operational Constraint', 'OPC', 'Degree to which a product or system constrains its operation to within safe parameters or states when encountering operational hazard.', 800);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=9), 'Risk Identification', 'RKI', 'Degree to which a product can identify a course of events or operations that can expose life, property or environment to unacceptable risk.', 801);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=9), 'Fail Safe', 'FSA', 'Degree to which a product can automatically place itself in a safe operating mode, or to revert to a safe condition in the event of a failure.', 802);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=9), 'Hazard warning', 'HWA', 'Degree to which a product or system provides warnings of unacceptable risks to operations or internal controls so that they can react in sufficient time to sustain safe operations.', 803);
INSERT INTO category_sub (category_id, name, short_name, description, sort_order) values ((select id from category where ordinal=9), 'Safe Integration', 'SIN', 'Degree to which a product can maintain safety during and after integration with one or more components.', 804);
