package com.agrupae.infrastructure.persistence.jpa.repository.course;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.domain.course.CourseMembership;
import com.agrupae.infrastructure.persistence.jpa.mapper.CourseMembershipJpaEntityMapper;
import com.agrupae.infrastructure.persistence.jpa.model.course.CourseMembershipJpaEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CourseMembershipRepositoryJpaAdapter implements CourseMembershipRepository {
    private final CourseMembershipJpaRepository jpaRepository;
    private final CourseMembershipJpaEntityMapper mapper;

    @Override
    public boolean exists(UUID studentId, UUID courseId) {
        return this.jpaRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public CourseMembership save(CourseMembership membership) {
        CourseMembershipJpaEntity entity = this.mapper.toJpaEntity(membership);
        CourseMembershipJpaEntity saved = this.jpaRepository.save(entity);
        return this.mapper.toDomain(saved);
    }
}
