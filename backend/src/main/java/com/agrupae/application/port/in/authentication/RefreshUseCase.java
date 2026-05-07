package com.agrupae.application.port.in.authentication;

import java.util.UUID;

import com.agrupae.domain.refresh_token.TokenPair;

public interface RefreshUseCase {
    public TokenPair handle(UUID userId, String rawRefreshToken);
}
