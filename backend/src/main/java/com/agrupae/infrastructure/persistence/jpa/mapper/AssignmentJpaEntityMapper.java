package com.agrupae.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.agrupae.domain.assignment.Assignment;
import com.agrupae.infrastructure.persistence.jpa.model.assignment.AssignmentJpaEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AssignmentJpaEntityMapper {
    private final AssignmentFlagsEmbeddableMapper assignmentFlagsEmbeddableMapper;

    public Assignment toDomain(AssignmentJpaEntity entity) {
        return Assignment.reconstruct(
            entity.getId(),
            entity.getCourseId(),
            entity.getName(),
            entity.getDescription(),
            this.assignmentFlagsEmbeddableMapper.toDomain(entity.getAssignmentFlags()),
            entity.isArchived(),
            entity.getDueDate(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public AssignmentJpaEntity toEntity(Assignment assignment) {
        return AssignmentJpaEntity.builder()
                .id(assignment.getId())
                .courseId(assignment.getCourseId())
                .name(assignment.getName())
                .description(assignment.getDescription())
                .assignmentFlags(this.assignmentFlagsEmbeddableMapper.toEmbeddable(assignment.getAssignmentFlags()))
                .archived(assignment.isArchived())
                .dueDate(assignment.getDueDate())
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}
