package com.agrupae.domain.user;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.exception.DomainException;
import com.agrupae.domain.role.Role;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class User {
    private final UUID id;
    private String name;
    private String email;
    private String passwordHash;
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private User(
            @NonNull final UUID id,
            @NonNull final String name,
            @NonNull final String email,
            @NonNull final String passwordHash,
            @NonNull final Role role,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt) {
        if (name.isBlank())
            throw new DomainException("User name cannot be blank.");
        if (email.isBlank())
            throw new DomainException("Email  cannot be blank.");
        if (!email.matches("[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new DomainException("Invalid email format.");
        }
        if (passwordHash.isBlank())
            throw new DomainException("Password name cannot be blank.");
        if (updatedAt.isBefore(createdAt))
            throw new DomainException("Update timestamp cannot be before creation timestamp.");

        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(
            final String name,
            final String email,
            final String passwordHash,
            final Role role) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .passwordHash(passwordHash)
                .role(role)
                .createdAt(now)
                .updatedAt(now)
                .build();
    };

    public static User reconstruct(
            final UUID id,
            final String name,
            final String email,
            final String passwordHash,
            final Role role,
            final Instant createdAt,
            final Instant updatedAt) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .passwordHash(passwordHash)
                .role(role)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public void updateProfile(String name, String email) {
        if (name.isBlank())
            throw new DomainException("User name cannot be blank.");
        if (email.isBlank())
            throw new DomainException("Email  cannot be blank.");
        if (!email.matches("[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new DomainException("Invalid email format.");
        }

        this.name = name;
        this.email = email;
        this.updatedAt = Instant.now();
    }

}
