CREATE TABLE IF NOT EXISTS poll (
    subject VARCHAR(255) NOT NULL,
    quota INTEGER,
    vote_ids BIGINT[],
    chat_id BIGINT NOT NULL,
    votes INTEGER NOT NULL,

    FOREIGN KEY (chat_id) REFERENCES chat(id),

    CONSTRAINT poll_pkey PRIMARY KEY (chat_id, subject),
    CONSTRAINT poll_subject_check CHECK (subject::text = 'REQUEST_AI_MESSAGE'::text)
);