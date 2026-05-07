package com.agrupae.infrastructure.persistence.jpa.repository.refresh_token;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agrupae.application.port.out.authentication.RefreshTokenRepository;
import com.agrupae.domain.refresh_token.RefreshToken;
import com.agrupae.infrastructure.persistence.jpa.mapper.RefreshTokenJpaEntityMapper;
import com.agrupae.infrastructure.persistence.jpa.model.refresh_token.RefreshTokenJpaEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryJpaAdapter implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final RefreshTokenJpaEntityMapper mapper;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = this.mapper.toJpaEntity(refreshToken);
        RefreshTokenJpaEntity savedEntity = this.refreshTokenJpaRepository.save(entity);
        return this.mapper.toDomain(savedEntity);
    }

    @Override
    public RefreshToken findById(UUID id) {
        return this.refreshTokenJpaRepository.findById(id)
            .map(this.mapper::toDomain)
            .orElse(null);
    }

    @Override
    public RefreshToken findByTokenHash(String tokenHash) {
        return this.refreshTokenJpaRepository.findByTokenHash(tokenHash)
            .map(this.mapper::toDomain)
            .orElse(null);
    }

    @Override
    @Transactional
    public void revokeAllByFamilyId(UUID familyId) {
        this.refreshTokenJpaRepository.revokeAllByFamilyId(familyId);
    }
}
