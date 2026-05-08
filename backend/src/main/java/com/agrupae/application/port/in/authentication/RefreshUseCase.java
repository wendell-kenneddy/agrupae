package com.agrupae.application.port.in.authentication;

import com.agrupae.domain.refresh_token.TokenPair;

public interface RefreshUseCase {
    public TokenPair handle(String rawRefreshToken);
}
