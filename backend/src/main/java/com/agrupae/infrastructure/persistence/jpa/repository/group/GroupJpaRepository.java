package com.agrupae.infrastructure.persistence.jpa.repository.group;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrupae.infrastructure.persistence.jpa.model.group.GroupJpaEntity;

@Repository
public interface GroupJpaRepository extends JpaRepository<GroupJpaEntity, UUID> {
    int countByAssignmentId(UUID assignmentId);
}
