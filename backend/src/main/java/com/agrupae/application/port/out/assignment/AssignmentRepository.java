package com.agrupae.application.port.out.assignment;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.agrupae.domain.assignment.Assignment;

public interface AssignmentRepository {
    Assignment save( Assignment assignment );

    Assignment findById(UUID id);

    List<Assignment> findByCourseId(UUID courseId);

    void delete(UUID id);

    List<Assignment> saveAll(List<Assignment> assignments);

    Page<Assignment> findByCourseId(UUID courseId, Pageable pageable);
}
