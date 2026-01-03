CREATE TABLE IF NOT EXISTS announcements
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id    BIGINT        NOT NULL,
    title       VARCHAR(50)   NOT NULL,
    content     VARCHAR(2047) NOT NULL,
    created_at  TIMESTAMP(6)  NOT NULL,
    modified_at TIMESTAMP(6)  NOT NULL,
    view_count  INT           NOT NULL DEFAULT 0,
    CONSTRAINT fk_announcements_group FOREIGN KEY (group_id) REFERENCES `groups` (id) ON DELETE CASCADE
);

CREATE INDEX idx_announcements_group_id ON announcements (group_id);
CREATE INDEX idx_announcements_created_at ON announcements (created_at DESC);
