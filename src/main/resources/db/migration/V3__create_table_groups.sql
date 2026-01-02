CREATE TABLE IF NOT EXISTS `groups`
(
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(50)  NOT NULL,
    code   CHAR(6)      NOT NULL UNIQUE,
    detail VARCHAR(255) NOT NULL
);

CREATE INDEX idx_groups_code ON `groups` (code);
