package com.agrupae.infrastructure.persistence.jpa.repository.course;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrupae.infrastructure.persistence.jpa.model.course.CourseJpaEntity;

public interface CourseJpaRepository extends JpaRepository<CourseJpaEntity, UUID> {

    Optional<CourseJpaEntity> findByInviteCode(String inviteCode);
}
