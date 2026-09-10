CREATE TABLE fraud_investigation_audits (
    transaction_id UUID PRIMARY KEY,
    risk_level VARCHAR(16) NOT NULL,
    recommended_action VARCHAR(64) NOT NULL,
    rationale VARCHAR(500) NOT NULL,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE fraud_investigation_evidence (
    transaction_id UUID NOT NULL REFERENCES fraud_investigation_audits(transaction_id),
    evidence VARCHAR(120) NOT NULL,
    CONSTRAINT pk_fraud_investigation_evidence PRIMARY KEY (transaction_id, evidence)
);