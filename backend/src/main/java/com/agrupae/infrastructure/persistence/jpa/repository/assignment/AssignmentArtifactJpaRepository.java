package com.agrupae.infrastructure.persistence.jpa.repository.assignment;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrupae.infrastructure.persistence.jpa.model.assignment.AssignmentArtifactJpaEntity;

public interface AssignmentArtifactJpaRepository extends JpaRepository<AssignmentArtifactJpaEntity, UUID> {
    List<AssignmentArtifactJpaEntity> findByAssignmentId(UUID assignmentId);
}
