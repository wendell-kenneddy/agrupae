ALTER TABLE groups_entry_requests
    ADD COLUMN id UUID NOT NULL DEFAULT gen_random_uuid(),
    ADD CONSTRAINT groups_entry_requests_pkey PRIMARY KEY (id);

ALTER TABLE groups_entry_requests RENAME COLUMN member_id TO user_id;
