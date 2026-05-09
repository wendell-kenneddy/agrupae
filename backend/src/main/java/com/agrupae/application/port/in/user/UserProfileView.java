package com.agrupae.application.port.in.user;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.role.Role;

public record UserProfileView(
    UUID id,
    String name,
    String email,
    Role role,
    Instant createdAt,
    Instant updatedAt) {
    
}
