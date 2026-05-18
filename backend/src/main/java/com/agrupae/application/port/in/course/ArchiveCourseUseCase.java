package com.agrupae.application.port.in.course;

import java.util.UUID;

import com.agrupae.domain.role.Role;

public interface ArchiveCourseUseCase {
    void handle(UUID actorId, Role actorRole, UUID courseId);
}
