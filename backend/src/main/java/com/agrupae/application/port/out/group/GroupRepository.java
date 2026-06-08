package com.agrupae.application.port.out.group;

import java.util.UUID;

import com.agrupae.domain.group.Group;

public interface GroupRepository {
    Group save(Group group);

    Group findById(UUID id);

    int countByAssignmentId(UUID assignmentId);
}
