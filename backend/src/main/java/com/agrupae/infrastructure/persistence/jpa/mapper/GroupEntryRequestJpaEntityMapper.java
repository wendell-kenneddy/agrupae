package com.agrupae.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.agrupae.domain.group.GroupEntryRequest;
import com.agrupae.infrastructure.persistence.jpa.model.group.GroupEntryRequestJpaEntity;

@Component
public class GroupEntryRequestJpaEntityMapper {

    public GroupEntryRequest toDomain(GroupEntryRequestJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return GroupEntryRequest.reconstruct(
            entity.getId(),
            entity.getGroupId(),
            entity.getUserId(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public GroupEntryRequestJpaEntity toEntity(GroupEntryRequest domain) {
        if (domain == null) {
            return null;
        }
        return GroupEntryRequestJpaEntity.builder()
                .id(domain.getId())
                .groupId(domain.getGroupId())
                .userId(domain.getUserId())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
