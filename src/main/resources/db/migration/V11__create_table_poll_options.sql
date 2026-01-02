CREATE TABLE IF NOT EXISTS poll_options
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    poll_id BIGINT      NOT NULL,
    content VARCHAR(30) NOT NULL,
    CONSTRAINT fk_poll_options_poll FOREIGN KEY (poll_id) REFERENCES polls (id) ON DELETE CASCADE
);

CREATE INDEX idx_poll_options_poll_id ON poll_options (poll_id);
