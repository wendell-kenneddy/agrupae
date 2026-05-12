package com.agrupae.application.port.in.user;

import java.util.UUID;

public interface UpdateProfileUseCase {
    UserProfileView handle(UUID userId, String name, String email);
}
