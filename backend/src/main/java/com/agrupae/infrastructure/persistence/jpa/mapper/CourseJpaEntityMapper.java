package com.agrupae.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.agrupae.domain.course.Course;
import com.agrupae.infrastructure.persistence.jpa.model.course.CourseJpaEntity;

@Component
public class CourseJpaEntityMapper {

    public Course toDomain(CourseJpaEntity entity) {
        return Course.reconstruct(
                entity.getId(),
                entity.getLeaderId(),
                entity.getName(),
                entity.getDescription(),
                entity.getInviteCode(),
                entity.isArchived(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public CourseJpaEntity toJpaEntity(Course course) {
        return CourseJpaEntity.builder()
                .id(course.getId())
                .leaderId(course.getLeaderId())
                .inviteCode(course.getInviteCode())
                .name(course.getName())
                .description(course.getDescription())
                .archived(course.isArchived())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}
