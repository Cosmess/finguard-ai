CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    merchant_id VARCHAR(80) NOT NULL,
    customer_id VARCHAR(80) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_method VARCHAR(40) NOT NULL,
    device_id VARCHAR(120),
    ip_address VARCHAR(45),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(80) NOT NULL,
    event_type VARCHAR(120) NOT NULL,
    topic VARCHAR(120) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_transactions_customer_created_at ON transactions (customer_id, created_at);
CREATE INDEX idx_outbox_events_status_created_at ON outbox_events (status, created_at);
