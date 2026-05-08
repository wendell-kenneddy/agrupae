package com.agrupae.application.port.in.authentication;

import com.agrupae.application.port.out.authentication.TokenPair;

public interface SignupUseCase {
    public TokenPair handle(String name, String email, String password);
}
