package com.agrupae.infrastructure.persistence.jpa.repository.course;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.agrupae.infrastructure.persistence.jpa.model.course.CourseJpaEntity;

public interface CourseJpaRepository extends JpaRepository<CourseJpaEntity, UUID> {

    Optional<CourseJpaEntity> findByInviteCode(String inviteCode);
    
    Page<CourseJpaEntity> findAllByIdIn(List<UUID> courseIds, Pageable pageable);
}
