CREATE TABLE IF NOT EXISTS message (
    id BIGINT NOT NULL UNIQUE,
    content VARCHAR(20000) NOT NULL,
    bot BOOLEAN NOT NULL,
    chat_id BIGINT NOT NULL,
    account_id BIGINT,

    CONSTRAINT message_pkey PRIMARY KEY (id),

    FOREIGN KEY (account_id) REFERENCES account(id),
    FOREIGN KEY (chat_id) REFERENCES chat(id)
);