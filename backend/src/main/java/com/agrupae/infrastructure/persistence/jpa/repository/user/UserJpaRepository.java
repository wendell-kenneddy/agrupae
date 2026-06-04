package com.agrupae.infrastructure.persistence.jpa.repository.user;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrupae.infrastructure.persistence.jpa.model.user.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);
    Page<UserJpaEntity> findAllByIdIn(List<UUID> ids, Pageable pageable);
}
