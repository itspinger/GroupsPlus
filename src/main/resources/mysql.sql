CREATE TABLE IF NOT EXISTS gp_users(
    `uuid` VARCHAR(36) PRIMARY KEY NOT NULL
);

CREATE TABLE IF NOT EXISTS gp_groups(
    `name` VARCHAR(64) PRIMARY KEY NOT NULL,
    `prefix` VARCHAR(64) NOT NULL,
    `priority` BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS gp_users_groups(
    `uuid` VARCHAR(36) NOT NULL,
    `name` VARCHAR(64) NOT NULL,
    `expiresAt` BIGINT NOT NULL,
    PRIMARY KEY (`uuid`, `name`, `expiresAt`),
    FOREIGN KEY (`uuid`) REFERENCES gp_users(`uuid`),
    FOREIGN KEY (`name`) REFERENCES gp_groups(`name`)
);