package com.agrupae.domain.refresh_token;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.exception.DomainException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class RefreshToken {
    private final UUID id;
    private final UUID userId;
    private final UUID tokenFamilyId;
    private final String tokenHash;
    private boolean revoked;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private RefreshToken(
            @NonNull final UUID id,
            @NonNull final UUID userId,
            @NonNull final UUID tokenFamilyId,
            @NonNull final String tokenHash,
            boolean revoked,
            @NonNull Duration duration,
            @NonNull final Instant createdAt,
            @NonNull Instant updatedAt
        ) {
        if (updatedAt.isBefore(createdAt)) throw new DomainException("Update timestamp cannot be before creation timestamp.");
        if (tokenHash.isBlank()) throw new DomainException("Token hash cannot be blank.");
        Instant expiresAt = createdAt.plus(duration);
        if (expiresAt.isBefore(createdAt)) throw new DomainException("Refresh token must expire after creation timestamp.");

        this.id = id;
        this.userId = userId;
        this.tokenFamilyId = tokenFamilyId;
        this.tokenHash = tokenHash;
        this.revoked = revoked;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RefreshToken create(UUID userId, String tokenHash, Duration duration) {
        UUID familyId = UUID.randomUUID();
        return RefreshToken.buildToken(userId, tokenHash, familyId, duration);
    }

    public static RefreshToken createForRotation(UUID userId, String tokenHash, UUID familyId, Duration duration) {
        return RefreshToken.buildToken(userId, tokenHash, familyId, duration);
    }

    private static RefreshToken buildToken(UUID userId, String tokenHash, UUID familyId, Duration duration) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        return RefreshToken.builder()
                .id(id)
                .userId(userId)
                .tokenFamilyId(familyId)
                .tokenHash(tokenHash)
                .duration(duration)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void revoke() {
        if (this.revoked)
            return;

        this.revoked = true;
        this.updatedAt = Instant.now();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }
}
