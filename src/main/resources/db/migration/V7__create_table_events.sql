CREATE TABLE IF NOT EXISTS events
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id          BIGINT        NOT NULL,
    start_time        TIMESTAMP(6)  NOT NULL,
    end_time          TIMESTAMP(6)  NOT NULL,
    register_deadline TIMESTAMP(6)  NOT NULL,
    detail            VARCHAR(2047) NOT NULL,
    capacity          INT           NOT NULL,
    CONSTRAINT fk_events_group FOREIGN KEY (group_id) REFERENCES `groups` (id) ON DELETE CASCADE
);

CREATE INDEX idx_events_group_id ON events (group_id);
CREATE INDEX idx_events_start_time ON events (start_time);
