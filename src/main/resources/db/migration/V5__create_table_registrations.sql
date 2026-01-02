CREATE TABLE IF NOT EXISTS registrations
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    group_id   BIGINT       NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_registrations_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_registrations_group FOREIGN KEY (group_id) REFERENCES `groups` (id) ON DELETE CASCADE,
    CONSTRAINT uk_registrations_user_group UNIQUE (user_id, group_id)
);

CREATE INDEX idx_registrations_group_id ON registrations (group_id);
CREATE INDEX idx_registrations_user_id ON registrations (user_id);
