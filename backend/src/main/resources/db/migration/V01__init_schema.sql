CREATE TYPE ROLES AS ENUM('ADMIN', 'USER');

CREATE TYPE GROUP_ENTRY_REQUEST_STATUS AS ENUM('PENDING', 'ACCEPTED', 'REJECTED');


CREATE TABLE users(
    id UUID PRIMARY KEY,
    user_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    user_role ROLES NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE courses(
    id UUID PRIMARY KEY,
    supervisor_id UUID REFERENCES users(id),
    invite_code TEXT NOT NULL UNIQUE,
    course_name TEXT NOT NULL,
    course_description TEXT,
    archived BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE courses_artifacts(
    id UUID PRIMARY KEY,
    course_id UUID REFERENCES courses(id),
    course_artifacts_name TEXT NOT NULL,
    course_artifacts_description TEXT,
    link TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE users_courses(
    studant_id UUID REFERENCES users(id),
    course_id UUID REFERENCES courses(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE assignments(
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL REFERENCES courses(id),
    assignment_name TEXT NOT NULL,
    assignment_description TEXT NOT NULL,
    archived BOOLEAN NOT NULL,
    max_group_members INTEGER NOT NULL,
    max_group INTEGER NOT NULL,
    studants_can_create_groups BOOLEAN NOT NULL,
    studants_can_leave_groups BOOLEAN NOT NULL,
    group_leader_can_dissolve BOOLEAN NOT NULL,
    group_leader_can_remove_members BOOLEAN NOT NULL,
    group_leader_can_change_mode BOOLEAN NOT NULL,
    group_leader_can_transfer_leadership BOOLEAN NOT NULL,
    course_supervisor_can_edit_assignment BOOLEAN NOT NULL,
    due_date TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE groups(
    id UUID PRIMARY KEY,
    assingment_id UUID NOT NULL REFERENCES assignments(id),
    leader_id UUID NOT NULL REFERENCES users(id),
    group_name TEXT NOT NULL,
    is_open BOOLEAN NOT NULL,
    members_can_edit_artifacts BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE groups_artifacts(
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL REFERENCES groups(id),
    group_artifact_name TEXT NOT NULL,
    group_artifact_description TEXT,
    link TEXT NOT NULL,
    private_artifact BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE groups_members(
    group_id UUID NOT NULL REFERENCES groups(id),
    member_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE groups_entry_requests(
    group_id UUID NOT NULL REFERENCES groups(id),
    member_id UUID NOT NULL REFERENCES users(id),
    group_entry_request_status GROUP_ENTRY_REQUEST_STATUS NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);