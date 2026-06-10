ALTER TABLE groups_members
    DROP CONSTRAINT groups_members_group_id_fkey,
    ADD CONSTRAINT groups_members_group_id_fkey
        FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE;

ALTER TABLE groups_entry_requests
    DROP CONSTRAINT groups_entry_requests_group_id_fkey,
    ADD CONSTRAINT groups_entry_requests_group_id_fkey
        FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE;

ALTER TABLE groups_artifacts
    DROP CONSTRAINT groups_artifacts_group_id_fkey,
    ADD CONSTRAINT groups_artifacts_group_id_fkey
        FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE;
