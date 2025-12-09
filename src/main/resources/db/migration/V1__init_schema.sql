CREATE TABLE sellers
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(255) NOT NULL,
    contact_info      VARCHAR(500) NOT NULL,
    registration_date TIMESTAMP    NOT NULL,
    deleted           BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE transactions
(
    id               BIGSERIAL PRIMARY KEY,
    seller_id        BIGINT         NOT NULL,
    amount           NUMERIC(19, 2) NOT NULL,
    payment_type     VARCHAR(32)    NOT NULL,
    status           VARCHAR(16)    NOT NULL DEFAULT 'PENDING',
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    transaction_date TIMESTAMP,
    CONSTRAINT fk_transactions_seller
        FOREIGN KEY (seller_id) REFERENCES sellers (id)
);

-- Indexes for analytics
CREATE INDEX idx_transactions_seller_id
    ON transactions (seller_id);

CREATE INDEX idx_transaction_date
    ON transactions (transaction_date);

CREATE INDEX idx_transactions_seller_date
    ON transactions (seller_id, transaction_date);
