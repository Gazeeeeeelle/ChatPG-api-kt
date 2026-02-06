ALTER TABLE account
    ALTER COLUMN public_id SET NOT NULL,
    ADD UNIQUE (public_id);