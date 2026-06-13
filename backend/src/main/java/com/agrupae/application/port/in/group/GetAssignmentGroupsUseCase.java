package com.agrupae.application.port.in.group;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface GetAssignmentGroupsUseCase {
    AssignmentGroupsView handle(UUID userId, UUID courseId, UUID assignmentId, Pageable pageable);
}
