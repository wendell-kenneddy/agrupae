ALTER TABLE users_courses
    ALTER COLUMN student_id SET NOT NULL,
    ALTER COLUMN course_id SET NOT NULL,
    ADD CONSTRAINT users_courses_pkey PRIMARY KEY (student_id, course_id);

ALTER TABLE users_courses DROP COLUMN updated_at;
