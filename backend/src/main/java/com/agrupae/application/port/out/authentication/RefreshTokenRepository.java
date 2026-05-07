package com.agrupae.application.port.out.authentication;

import java.util.UUID;

import com.agrupae.domain.refresh_token.RefreshToken;

public interface RefreshTokenRepository {
    public RefreshToken save(RefreshToken refreshToken);

    public RefreshToken findById(UUID id);

    public RefreshToken findByTokenHash(String tokenHash);

    public void revokeAllByFamilyId(UUID familyId);
}
