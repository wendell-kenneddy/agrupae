package com.agrupae.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.agrupae.domain.course.LeadershipTransferRequest;
import com.agrupae.infrastructure.persistence.jpa.model.course.LeadershipTransferRequestJpaEntity;

@Component
public class LeadershipTransferRequestJpaEntityMapper {

    public LeadershipTransferRequest toDomain(LeadershipTransferRequestJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return LeadershipTransferRequest.reconstruct(
                entity.getId(),
                entity.getCourseId(),
                entity.getSenderId(),
                entity.getTargetId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public LeadershipTransferRequestJpaEntity toEntity(LeadershipTransferRequest domain) {
        if (domain == null) {
            return null;
        }
        return LeadershipTransferRequestJpaEntity.builder()
                .id(domain.getId())
                .courseId(domain.getCourseId())
                .senderId(domain.getSenderId())
                .targetId(domain.getTargetId())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
