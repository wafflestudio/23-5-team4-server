CREATE TABLE IF NOT EXISTS members
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id  BIGINT                                            NOT NULL,
    group_id BIGINT                                            NOT NULL,
    nickname VARCHAR(30)                                       NOT NULL,
    type     ENUM ('LEADER', 'CO_LEADER', 'ADMIN', 'MEMBER') NOT NULL,
    CONSTRAINT fk_members_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_members_group FOREIGN KEY (group_id) REFERENCES `groups` (id) ON DELETE CASCADE,
    CONSTRAINT uk_members_user_group UNIQUE (user_id, group_id)
);

CREATE INDEX idx_members_group_id ON members (group_id);
CREATE INDEX idx_members_user_id ON members (user_id);
