DROP TABLE IF EXISTS invitations;
CREATE TABLE invitations (
  `id` binary(16) NOT NULL DEFAULT (uuid_to_bin(uuid())),
  `org_id` binary(16) NOT NULL,
  `email` varchar(255) NOT NULL,
  `org_role` varchar(20) NOT NULL,
  `token_hash` varchar(64) NOT NULL UNIQUE,
  `expiration` datetime NOT NULL,
  `status` varchar(20) NOT NULL,
  `sender_email` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  INDEX idx_invitations_org_status (org_id, status),
  FOREIGN KEY (org_id) REFERENCES organizations(id) ON DELETE CASCADE
);
