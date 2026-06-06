package com.agrupae.infrastructure.persistence.jpa.repository.assignment;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.infrastructure.persistence.jpa.mapper.AssignmentJpaEntityMapper;
import com.agrupae.infrastructure.persistence.jpa.model.assignment.AssignmentJpaEntity;


import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AssignmentJpaRepositoryAdapter implements AssignmentRepository {
    private final AssignmentJpaRepository assignmentJpaRepository;
    private final AssignmentJpaEntityMapper assignmentJpaEntityMapper;

    @Override
    public Assignment save(Assignment assignment) {
        AssignmentJpaEntity entity = this.assignmentJpaEntityMapper.toEntity(assignment);
        AssignmentJpaEntity saved = this.assignmentJpaRepository.save(entity);
        return this.assignmentJpaEntityMapper.toDomain(saved);
    }

    @Override
    public Assignment findById(UUID id) {
        return this.assignmentJpaRepository
                .findById(id)
                .map(this.assignmentJpaEntityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<Assignment> findByCourseId(UUID courseId) {
        return this.assignmentJpaRepository
                .findByCourseId(courseId)
                .stream()
                .map(this.assignmentJpaEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        this.assignmentJpaRepository.deleteById(id);
    }

    @Override
    public List<Assignment> saveAll(List<Assignment> assignments) {
        List<AssignmentJpaEntity> entities = assignments.stream()
                .map(this.assignmentJpaEntityMapper::toEntity)
                .toList();
        List<AssignmentJpaEntity> saved = this.assignmentJpaRepository.saveAll(entities);
        return saved.stream()
                .map(this.assignmentJpaEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Assignment> findByCourseId(UUID courseId, Pageable pageable) {
        return this.assignmentJpaRepository
                .findByCourseId(courseId, pageable)
                .map(this.assignmentJpaEntityMapper::toDomain);
    }
}
