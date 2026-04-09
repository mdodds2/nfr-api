CREATE TABLE `nfr`.`ReportsRequirements` (
  `report_id` BINARY(16) NOT NULL,
  `requirement_id` BINARY(16) NOT NULL,

  PRIMARY KEY (report_id, requirement_id),

  FOREIGN KEY (report_id) REFERENCES reports(id) ON DELETE CASCADE,
  FOREIGN KEY (requirement_id) REFERENCES requirements(nfr_id) ON DELETE CASCADE
);
