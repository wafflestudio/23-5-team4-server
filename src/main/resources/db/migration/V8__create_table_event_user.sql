CREATE TABLE IF NOT EXISTS event_user
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT                             NOT NULL,
    event_id       BIGINT                             NOT NULL,
    status         ENUM ('FINALIZED', 'WAITING')     NOT NULL,
    waiting_number INT,
    CONSTRAINT fk_event_user_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_user_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT uk_event_user_user_event UNIQUE (user_id, event_id)
);

CREATE INDEX idx_event_user_event_id ON event_user (event_id);
CREATE INDEX idx_event_user_user_id ON event_user (user_id);
