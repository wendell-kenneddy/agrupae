CREATE UNIQUE INDEX idx_unique_pending_transfer_per_course
    ON leadership_transfer_requests (course_id)
    WHERE status = 'PENDING';
