package com.agrupae.infrastructure.persistence.jpa.repository.assignment;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.agrupae.application.port.out.assignment.AssignmentArtifactRepository;
import com.agrupae.domain.assignment.AssignmentArtifact;
import com.agrupae.infrastructure.persistence.jpa.mapper.AssignmentArtifactJpaEntityMapper;
import com.agrupae.infrastructure.persistence.jpa.model.assignment.AssignmentArtifactJpaEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AssignmentArtifactJpaRepositoryAdapter implements AssignmentArtifactRepository {
    private final AssignmentArtifactJpaRepository assignmentArtifactJpaRepository;
    private final AssignmentArtifactJpaEntityMapper assignmentArtifactJpaEntityMapper;

    @Override
    public AssignmentArtifact save(AssignmentArtifact artifact) {
        AssignmentArtifactJpaEntity entity = this.assignmentArtifactJpaEntityMapper.toEntity(artifact);
        AssignmentArtifactJpaEntity saved = this.assignmentArtifactJpaRepository.save(entity);
        return this.assignmentArtifactJpaEntityMapper.toDomain(saved);
    }

    @Override
    public List<AssignmentArtifact> findByAssignmentId(UUID assignmentId) {
        return this.assignmentArtifactJpaRepository
                .findByAssignmentId(assignmentId)
                .stream()
                .map(this.assignmentArtifactJpaEntityMapper::toDomain)
                .toList();
    }

    @Override
    public AssignmentArtifact findById(UUID id) {
        return this.assignmentArtifactJpaRepository
                .findById(id)
                .map(this.assignmentArtifactJpaEntityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public void delete(UUID id) {
        this.assignmentArtifactJpaRepository.deleteById(id);
    }
}
