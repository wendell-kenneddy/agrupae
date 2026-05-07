package com.agrupae.infrastructure.persistence.jpa.repository.refresh_token;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrupae.infrastructure.persistence.jpa.model.refresh_token.RefreshTokenJpaEntity;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity rt SET rt.revoked = true WHERE rt.tokenFamilyId = :tokenFamilyId")
    void revokeAllByFamilyId(@Param("tokenFamilyId") UUID tokenFamilyId);
}
