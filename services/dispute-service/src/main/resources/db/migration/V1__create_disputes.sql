CREATE TABLE disputes (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    customer_id VARCHAR(120) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(32) NOT NULL,
    reviewer_id VARCHAR(120),
    review_decision VARCHAR(32),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE dispute_evidence (
    id UUID PRIMARY KEY,
    dispute_id UUID NOT NULL REFERENCES disputes(id),
    type VARCHAR(120) NOT NULL,
    reference VARCHAR(500) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    added_at TIMESTAMP WITH TIME ZONE NOT NULL
);