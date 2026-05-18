package com.agrupae.infrastructure.persistence.jpa.repository.course;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.infrastructure.persistence.jpa.mapper.CourseJpaEntityMapper;
import com.agrupae.infrastructure.persistence.jpa.model.course.CourseJpaEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryJpaAdapter implements CourseRepository {
    private final CourseJpaRepository courseJpaRepository;
    private final CourseJpaEntityMapper mapper;

    @Override
    public Course findById(UUID id) {
        return this.courseJpaRepository.findById(id)
                .map(this.mapper::toDomain)
                .orElse(null);
    }

    @Override
    public Course findByInviteCode(String inviteCode) {
        return this.courseJpaRepository.findByInviteCode(inviteCode)
                .map(this.mapper::toDomain)
                .orElse(null);
    }

    @Override
    public Course save(Course course) {
        CourseJpaEntity entity = this.mapper.toJpaEntity(course);
        CourseJpaEntity saved = this.courseJpaRepository.save(entity);
        return this.mapper.toDomain(saved);
    }
}
