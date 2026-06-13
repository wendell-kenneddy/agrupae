package com.agrupae.application.port.out.group;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.agrupae.domain.group.Group;

public interface GroupRepository {
    Group save(Group group);

    Group findById(UUID id);

    int countByAssignmentId(UUID assignmentId);

    void deleteById(UUID id);

    Page<Group> findByAssignmentId(UUID assignmentId, Pageable pageable);
}
