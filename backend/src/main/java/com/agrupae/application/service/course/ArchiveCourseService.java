package com.agrupae.application.service.course;

import java.util.UUID;

import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.NotAuthorizedToArchiveCourseException;
import com.agrupae.application.port.in.course.ArchiveCourseUseCase;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.role.Role;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArchiveCourseService implements ArchiveCourseUseCase {
    private final CourseRepository courseRepository;

    @Override
    public void handle(@NonNull UUID actorId, @NonNull Role actorRole, @NonNull UUID courseId) {
        Course course = this.courseRepository.findById(courseId);

        if (course == null)
            throw new CourseNotFoundException();

        if (actorRole != Role.ADMIN && !course.getLeaderId().equals(actorId))
            throw new NotAuthorizedToArchiveCourseException();

        course.archive();
        this.courseRepository.save(course);
    }
}
