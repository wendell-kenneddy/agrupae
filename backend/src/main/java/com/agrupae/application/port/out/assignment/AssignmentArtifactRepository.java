package com.agrupae.application.port.out.assignment;

import java.util.List;
import java.util.UUID;

import com.agrupae.domain.assignment.AssignmentArtifact;

public interface AssignmentArtifactRepository {
    AssignmentArtifact save(AssignmentArtifact assignmentArtifact);

    List<AssignmentArtifact> findByAssignmentId(UUID assignmentId);

    AssignmentArtifact findById(UUID id);

    void delete(UUID id);
}
