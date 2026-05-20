package com.agrupae.infrastructure.persistence.jpa.repository.course;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrupae.infrastructure.persistence.jpa.model.course.CourseMembershipId;
import com.agrupae.infrastructure.persistence.jpa.model.course.CourseMembershipJpaEntity;

public interface CourseMembershipJpaRepository
        extends JpaRepository<CourseMembershipJpaEntity, CourseMembershipId> {

    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

    List<CourseMembershipJpaEntity> findByStudentId(UUID studentId);
}
