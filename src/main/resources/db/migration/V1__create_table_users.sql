CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6)  NOT NULL,
    updated_at TIMESTAMP(6)  NOT NULL
);
