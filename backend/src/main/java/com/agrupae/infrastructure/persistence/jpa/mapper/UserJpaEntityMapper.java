package com.agrupae.infrastructure.persistence.jpa.mapper;

import com.agrupae.domain.user.User;
import com.agrupae.infrastructure.persistence.jpa.model.user.UserJpaEntity;

import org.springframework.stereotype.Component;

@Component
public class UserJpaEntityMapper {

    public User toDomain(UserJpaEntity entity) {
        return User.reconstruct(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getPasswordHash(),
            entity.getRole(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public UserJpaEntity toJpaEntity(User user) {
        return UserJpaEntity.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .passwordHash(user.getPasswordHash())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
