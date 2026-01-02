CREATE TABLE IF NOT EXISTS votes
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    poll_option_id BIGINT NOT NULL,
    member_id      BIGINT NOT NULL,
    CONSTRAINT fk_votes_poll_option FOREIGN KEY (poll_option_id) REFERENCES poll_options (id) ON DELETE CASCADE,
    CONSTRAINT fk_votes_member FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
);

CREATE INDEX idx_votes_poll_option_id ON votes (poll_option_id);
CREATE INDEX idx_votes_member_id ON votes (member_id);
