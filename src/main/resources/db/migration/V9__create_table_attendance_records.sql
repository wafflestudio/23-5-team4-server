CREATE TABLE IF NOT EXISTS attendance_records
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT                            NOT NULL,
    event_id   BIGINT                            NOT NULL,
    status     ENUM ('PRESENT', 'ABSENT', 'LATE') NOT NULL,
    created_at TIMESTAMP(6)                      NOT NULL,
    CONSTRAINT fk_attendance_records_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_records_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT uk_attendance_records_user_event UNIQUE (user_id, event_id)
);

CREATE INDEX idx_attendance_records_event_id ON attendance_records (event_id);
CREATE INDEX idx_attendance_records_user_id ON attendance_records (user_id);
