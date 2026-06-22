package com.agrupae.infrastructure.persistence.jpa.repository.course;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrupae.domain.course.LeadershipTransferRequestStatus;
import com.agrupae.infrastructure.persistence.jpa.model.course.LeadershipTransferRequestJpaEntity;

public interface LeadershipTransferRequestJpaRepository extends JpaRepository<LeadershipTransferRequestJpaEntity, UUID> {
    List<LeadershipTransferRequestJpaEntity> findByCourseId(UUID courseId);

    List<LeadershipTransferRequestJpaEntity> findByCourseIdAndStatus(UUID courseId, LeadershipTransferRequestStatus status);
}
