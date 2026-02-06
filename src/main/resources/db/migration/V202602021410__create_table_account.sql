CREATE TABLE IF NOT EXISTS account (
    id BIGINT NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    uuid UUID,
    email VARCHAR(255) UNIQUE NOT NULL,
    uuid_birth TIMESTAMP(6) WITH TIME ZONE,
    status VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) UNIQUE,
    a2f BOOLEAN NOT NULL,

    CONSTRAINT account_pkey PRIMARY KEY (id),
    CONSTRAINT account_status_check CHECK (status::text = ANY (ARRAY['DISABLED'::character varying, 'ENABLED'::character varying, 'DELETED'::character varying]::text[]))
);
