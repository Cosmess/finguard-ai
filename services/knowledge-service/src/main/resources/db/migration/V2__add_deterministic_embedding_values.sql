ALTER TABLE knowledge_documents
    ADD COLUMN embedding_values TEXT NOT NULL DEFAULT '0,0,1';