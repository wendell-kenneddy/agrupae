package com.agrupae.application.service.course;

import java.util.UUID;

import com.agrupae.application.port.in.course.CourseView;
import com.agrupae.application.port.in.course.CreateCourseUseCase;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateCourseService implements CreateCourseUseCase {
    private final CourseRepository courseRepository;

    @Override
    public CourseView handle(UUID leaderId, String name, String description) {
        Course course = Course.create(leaderId, name, description);
        Course saved = courseRepository.save(course);

        return new CourseView(
                saved.getId(),
                saved.getLeaderId(),
                saved.getName(),
                saved.getDescription(),
                saved.getInviteCode(),
                saved.isArchived(),
                saved.getCreatedAt(),
                saved.getUpdatedAt());
    }
}
