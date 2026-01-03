CREATE TABLE IF NOT EXISTS polls
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id   BIGINT       NOT NULL,
    title      VARCHAR(50)  NOT NULL,
    deadline   TIMESTAMP(6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_polls_group FOREIGN KEY (group_id) REFERENCES `groups` (id) ON DELETE CASCADE
);

CREATE INDEX idx_polls_group_id ON polls (group_id);
CREATE INDEX idx_polls_deadline ON polls (deadline);
CREATE INDEX idx_polls_created_at ON polls (created_at DESC);
