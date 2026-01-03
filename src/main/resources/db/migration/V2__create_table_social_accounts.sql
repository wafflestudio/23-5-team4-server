CREATE TABLE IF NOT EXISTS social_accounts
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT       NOT NULL,
    provider          VARCHAR(30)  NOT NULL,
    provider_user_id  VARCHAR(100) NOT NULL,
    created_at        TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_social_accounts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uk_social_accounts_provider UNIQUE (provider, provider_user_id)
);

CREATE INDEX idx_social_accounts_user_id ON social_accounts (user_id);
