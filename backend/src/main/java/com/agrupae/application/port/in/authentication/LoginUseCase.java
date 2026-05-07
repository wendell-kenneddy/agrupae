package com.agrupae.application.port.in.authentication;

import com.agrupae.domain.refresh_token.TokenPair;

public interface LoginUseCase {
    public TokenPair handle(String email, String password);
}
