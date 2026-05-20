package com.agrupae.application.port.out.course;

import java.util.UUID;

import com.agrupae.domain.course.Course;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseRepository {
    Course findById(UUID id);

    Course findByInviteCode(String inviteCode);

    Course save(Course course);

    Page<Course> findAllByIdIn(List<UUID> courseIds, Pageable pageable);
}
