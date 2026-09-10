CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE knowledge_documents (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    source_uri VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    embedding vector(3),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);