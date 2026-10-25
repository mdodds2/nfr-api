CREATE TABLE `nfr`.`password_reset_tokens` (
  `id` BINARY(16) NOT NULL DEFAULT (UUID_TO_BIN(UUID())),
  `user_id` BINARY(16) NOT NULL,
  `token_hash` CHAR(64) NOT NULL,
  `expires_at` TIMESTAMP NOT NULL,
  `used_at` TIMESTAMP NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_prt_token_hash` (`token_hash`),
  CONSTRAINT `fk_prt_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
);