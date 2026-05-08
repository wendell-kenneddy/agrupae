package com.agrupae.application.port.in.authentication;

public interface LogoutUseCase {
    void handle(String rawRefreshToken);
}
