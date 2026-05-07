CREATE TABLE IF NOT EXISTS refresh_tokens(
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    token_family_id UUID NOT NULL,
    token_hash TEXT NOT NULL,
    revoked BOOLEAN NOT NULL,
    expires_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);