package com.agrupae.infrastructure.persistence.jpa.repository.user;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.user.User;
import com.agrupae.infrastructure.persistence.jpa.mapper.UserJpaEntityMapper;
import com.agrupae.infrastructure.persistence.jpa.model.user.UserJpaEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJpaAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserJpaEntityMapper mapper;

    @Override
    public User findById(UUID id) {
        return this.userJpaRepository.findById(id)
            .map(this.mapper::toDomain)
            .orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return this.userJpaRepository.findByEmail(email)
            .map(this.mapper::toDomain)
            .orElse(null);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = this.mapper.toJpaEntity(user);
        UserJpaEntity saved = this.userJpaRepository.save(entity);
        return this.mapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        this.userJpaRepository.deleteById(id);
    }

    @Override
    public Page<User> findAllByIdIn(java.util.List<UUID> ids, Pageable pageable) {
        return this.userJpaRepository.findAllByIdIn(ids, pageable)
            .map(this.mapper::toDomain);
    }
}
