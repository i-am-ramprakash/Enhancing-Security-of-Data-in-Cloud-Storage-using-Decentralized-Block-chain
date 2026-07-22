-- Secure Cloud Storage schema (MySQL 8.4+). Do not import the legacy VTJNS04.sql dump.
CREATE DATABASE IF NOT EXISTS secure_cloud CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE secure_cloud;

CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL,
    age SMALLINT UNSIGNED NOT NULL,
    gender VARCHAR(30) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('USER','OWNER','ADMIN') NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uq_accounts_email (email),
    CONSTRAINT chk_accounts_age CHECK (age BETWEEN 13 AND 120)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS files (
    id CHAR(36) NOT NULL,
    owner_id BIGINT UNSIGNED NOT NULL,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(150) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    ciphertext LONGBLOB NOT NULL,
    file_nonce BINARY(12) NOT NULL,
    wrapped_key VARBINARY(64) NOT NULL,
    key_nonce BINARY(12) NOT NULL,
    ciphertext_sha256 BINARY(32) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_files_owner_created (owner_id, created_at),
    FULLTEXT KEY ft_files_search (filename, description),
    CONSTRAINT fk_files_owner FOREIGN KEY (owner_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS access_requests (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    file_id CHAR(36) NOT NULL,
    requester_id BIGINT UNSIGNED NOT NULL,
    status ENUM('PENDING','APPROVED','DENIED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    decided_at TIMESTAMP(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_access_file_requester (file_id, requester_id),
    KEY idx_access_requester (requester_id, status),
    CONSTRAINT fk_access_file FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE,
    CONSTRAINT fk_access_requester FOREIGN KEY (requester_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS audit_chain (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    previous_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    entry_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    entity_type VARCHAR(40) NOT NULL,
    entity_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    payload_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_audit_entry_hash (entry_hash),
    KEY idx_audit_entity (entity_type, entity_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS audit_head (
    id TINYINT UNSIGNED NOT NULL,
    current_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_audit_head_singleton CHECK (id = 1)
) ENGINE=InnoDB;
INSERT INTO audit_head(id,current_hash) VALUES(1,REPEAT('0',64))
ON DUPLICATE KEY UPDATE id=VALUES(id);

-- CREATE USER IF NOT EXISTS 'secure_cloud'@'localhost' IDENTIFIED BY 'REPLACE_WITH_A_LONG_RANDOM_PASSWORD';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON secure_cloud.* TO 'secure_cloud'@'localhost';
-- Generate a hash with com.security.PasswordTool, then create the first administrator:
-- INSERT INTO accounts(name,email,age,gender,password_hash,role)
-- VALUES ('Administrator','admin@example.com',18,'Prefer not to say','PASTE_PBKDF2_HASH','ADMIN');
