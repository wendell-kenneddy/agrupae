package com.agrupae.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.agrupae.domain.course.CourseMembership;
import com.agrupae.infrastructure.persistence.jpa.model.course.CourseMembershipJpaEntity;

@Component
public class CourseMembershipJpaEntityMapper {

    public CourseMembership toDomain(CourseMembershipJpaEntity entity) {
        return CourseMembership.reconstruct(
                entity.getStudentId(),
                entity.getCourseId(),
                entity.getCreatedAt());
    }

    public CourseMembershipJpaEntity toJpaEntity(CourseMembership membership) {
        return CourseMembershipJpaEntity.builder()
                .studentId(membership.getStudentId())
                .courseId(membership.getCourseId())
                .createdAt(membership.getCreatedAt())
                .build();
    }
}
