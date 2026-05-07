package com.agrupae.application.port.in.authentication;

import com.agrupae.domain.refresh_token.TokenPair;

public interface SignupUseCase {
    public TokenPair handle(String name, String email, String password);
}
