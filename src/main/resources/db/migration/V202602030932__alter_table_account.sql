ALTER TABLE account
    ALTER COLUMN uuid TYPE UUID
        USING uuid::uuid;

ALTER TABLE account
    DROP COLUMN uuid_birth;

ALTER TABLE account
    ADD request_handle UUID UNIQUE;
