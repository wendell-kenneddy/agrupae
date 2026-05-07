package com.agrupae.infrastructure.persistence.jpa.mapper;

import com.agrupae.domain.refresh_token.RefreshToken;
import com.agrupae.infrastructure.persistence.jpa.model.refresh_token.RefreshTokenJpaEntity;

import org.springframework.stereotype.Component;

@Component
public class RefreshTokenJpaEntityMapper {

    public RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return RefreshToken.reconstruct(
            entity.getId(),
            entity.getUserId(),
            entity.getTokenFamilyId(),
            entity.getTokenHash(),
            entity.isRevoked(),
            entity.getExpiresAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public RefreshTokenJpaEntity toJpaEntity(RefreshToken token) {
        return RefreshTokenJpaEntity.builder()
            .id(token.getId())
            .userId(token.getUserId())
            .tokenFamilyId(token.getTokenFamilyId())
            .tokenHash(token.getTokenHash())
            .revoked(token.isRevoked())
            .expiresAt(token.getExpiresAt())
            .createdAt(token.getCreatedAt())
            .updatedAt(token.getUpdatedAt())
            .build();
    }
}
