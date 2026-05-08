package com.agrupae.application.port.in.user;

import java.util.UUID;

public interface GetUserProfileUseCase {
    public UserProfileView handle(final UUID userId);
}
