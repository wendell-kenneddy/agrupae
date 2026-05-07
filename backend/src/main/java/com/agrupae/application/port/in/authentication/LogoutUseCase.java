package com.agrupae.application.port.in.authentication;

import java.util.UUID;

public interface LogoutUseCase {
    void handle(UUID userId, String rawRefreshToken);
}
