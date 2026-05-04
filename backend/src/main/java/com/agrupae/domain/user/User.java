package com.agrupae.domain.user;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.role.Role;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class User {
    private final UUID id;
    private  String name;
    private  String email;
    @Getter(value = AccessLevel.NONE)
    private  String passwordHash;
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;
}
