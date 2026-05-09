package com.agrupae.application.port.in.authentication;

import com.agrupae.application.port.out.authentication.TokenPair;

public interface LoginUseCase {
    public TokenPair handle(String email, String password);
}
