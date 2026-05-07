ALTER TABLE users RENAME COLUMN user_name TO "name";

ALTER TABLE users RENAME COLUMN user_role TO "role";

ALTER TABLE courses RENAME COLUMN course_name TO "name";

ALTER TABLE courses RENAME COLUMN course_description TO "description";

ALTER TABLE courses_artifacts RENAME COLUMN course_artifacts_name TO "name";

ALTER TABLE courses_artifacts RENAME COLUMN course_artifacts_description TO "description";

ALTER TABLE courses_artifacts RENAME COLUMN link TO "resource_link";

ALTER TABLE assignments RENAME COLUMN assignment_name TO "name";

ALTER TABLE assignments RENAME COLUMN assignment_description TO "description";

ALTER TABLE groups RENAME COLUMN assingment_id TO assignment_id;

ALTER TABLE groups RENAME COLUMN group_name TO "name";

ALTER TABLE groups_artifacts RENAME COLUMN group_artifact_name TO "name";

ALTER TABLE groups_artifacts RENAME COLUMN group_artifact_description TO "description";

ALTER TABLE groups_artifacts RENAME COLUMN link TO "resource_link";

ALTER TABLE users_courses RENAME COLUMN studant_id TO student_id;

ALTER TABLE assignments RENAME COLUMN studants_can_create_groups TO students_can_create_groups;

ALTER TABLE assignments RENAME COLUMN studants_can_leave_groups TO students_can_leave_groups;
