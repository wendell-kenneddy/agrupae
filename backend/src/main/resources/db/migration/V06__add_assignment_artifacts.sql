CREATE TABLE assignment_artifacts(
    id UUID PRIMARY KEY,
    assignment_id UUID REFERENCES assignments(id),
    assignment_artifact_name TEXT NOT NULL,
    assignment_artifact_description TEXT,
    link TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);