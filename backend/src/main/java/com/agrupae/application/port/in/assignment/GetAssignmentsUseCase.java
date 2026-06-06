package com.agrupae.application.port.in.assignment;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface GetAssignmentsUseCase {
    public Page<AssignmentView> handle(UUID studentId, UUID courseId, Pageable pageable);
}
