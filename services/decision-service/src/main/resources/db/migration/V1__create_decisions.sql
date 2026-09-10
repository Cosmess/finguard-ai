CREATE TABLE decisions (
    decision_id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    outcome VARCHAR(32) NOT NULL,
    rationale VARCHAR(500) NOT NULL,
    decided_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE decision_signals (
    decision_id UUID NOT NULL REFERENCES decisions(decision_id),
    signals VARCHAR(120) NOT NULL
);