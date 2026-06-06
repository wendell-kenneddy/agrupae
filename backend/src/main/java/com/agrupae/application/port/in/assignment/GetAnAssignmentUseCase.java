package com.agrupae.application.port.in.assignment;

import java.util.UUID;

public interface GetAnAssignmentUseCase {
    public AssignmentView handle(UUID studentId, UUID courseId, UUID assignmentId);
}
