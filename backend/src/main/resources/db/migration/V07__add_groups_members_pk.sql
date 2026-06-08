ALTER TABLE groups_members
    ALTER COLUMN group_id SET NOT NULL,
    ALTER COLUMN member_id SET NOT NULL,
    ADD CONSTRAINT groups_members_pkey PRIMARY KEY (group_id, member_id);

ALTER TABLE groups_members DROP COLUMN updated_at;
