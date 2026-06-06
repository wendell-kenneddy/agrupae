package com.agrupae.infrastructure.persistence.jpa.repository.assignment;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.agrupae.infrastructure.persistence.jpa.model.assignment.AssignmentJpaEntity;

@Repository
public interface AssignmentJpaRepository extends JpaRepository<AssignmentJpaEntity, UUID> {
    List<AssignmentJpaEntity> findByCourseId(UUID courseId);

    Page<AssignmentJpaEntity> findByCourseId(UUID courseId, Pageable pageable);
}
