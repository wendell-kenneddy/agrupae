package com.agrupae.application.port.out.assignment;

import java.util.List;
import java.util.UUID;

import com.agrupae.domain.assignment.Assignment;

public interface AssignmentRepository {
    Assignment save( Assignment assignment );

    Assignment findById(UUID id);

    List<Assignment> findByCourseId(UUID courseId);

    void delete(UUID id);
}
