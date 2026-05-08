package com.agrupae.application.port.in.authentication;

import com.agrupae.application.port.out.authentication.TokenPair;

public interface RefreshUseCase {
    public TokenPair handle(String rawRefreshToken);
}
