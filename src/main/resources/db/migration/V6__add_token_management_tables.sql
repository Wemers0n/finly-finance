CREATE TABLE tb_revoked_tokens (
    id UUID PRIMARY KEY,
    token TEXT NOT NULL,
    revoked_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

CREATE TABLE tb_refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token TEXT NOT NULL,
    issued_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES tb_users(id)
);

CREATE INDEX idx_revoked_tokens_token ON tb_revoked_tokens(token);
CREATE INDEX idx_refresh_tokens_token ON tb_refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON tb_refresh_tokens(user_id);
