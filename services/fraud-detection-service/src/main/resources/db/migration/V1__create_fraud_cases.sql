CREATE TABLE processed_transactions (
    transaction_id UUID PRIMARY KEY,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE fraud_cases (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL UNIQUE,
    customer_id VARCHAR(80) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    score INTEGER NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    triggered_rules TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_fraud_cases_risk_created_at ON fraud_cases (risk_level, created_at);

CREATE TABLE velocity_snapshots (
    dimension VARCHAR(40) NOT NULL,
    dimension_value VARCHAR(120) NOT NULL,
    transaction_count BIGINT NOT NULL,
    window_start TIMESTAMP WITH TIME ZONE NOT NULL,
    window_end TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (dimension, dimension_value)
);
