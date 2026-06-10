CREATE TABLE email_outbox (
    id BIGINT NOT NULL AUTO_INCREMENT,
    message_key VARCHAR(191) NOT NULL,
    event_type ENUM(
        'REGISTRATION_STATUS',
        'REGISTRATION_DELETE',
        'WAITLIST_PROMOTION',
        'REGISTRATION_DEMOTION',
        'EVENT_CANCELLATION'
    ) NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    payload_json JSON NOT NULL,
    status ENUM('PENDING', 'PROCESSING', 'SENT', 'FAILED') NOT NULL DEFAULT 'PENDING',
    retry_count INT NOT NULL DEFAULT 0,
    max_retry_count INT NOT NULL DEFAULT 5,
    next_retry_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    last_error VARCHAR(1000) NULL,
    sent_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_email_outbox_message_key UNIQUE (message_key)
);

CREATE INDEX idx_email_outbox_status_next_retry_at_id
    ON email_outbox (status, next_retry_at, id);

CREATE INDEX idx_email_outbox_created_at
    ON email_outbox (created_at);
