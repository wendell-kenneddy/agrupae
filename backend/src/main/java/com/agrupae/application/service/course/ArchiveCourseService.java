package com.agrupae.application.service.course;

import java.util.List;
import java.util.UUID;

import com.agrupae.application.exception.course.CourseArchivedException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.NotAuthorizedToArchiveCourseException;
import com.agrupae.application.port.in.course.ArchiveCourseUseCase;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.role.Role;
import com.agrupae.application.port.out.course.CourseMembershipRepository;

import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArchiveCourseService implements ArchiveCourseUseCase {
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final CourseMembershipRepository courseMembershipRepository;

    @Override
    @Transactional
    public void handle(@NonNull UUID actorId, @NonNull Role actorRole, @NonNull UUID courseId) {
        Course course = this.courseRepository.findById(courseId);

        if (course == null || !courseMembershipRepository.exists(actorId, courseId)) {
            throw new CourseNotFoundException();
        }

        if (course.isArchived()) {
            throw new CourseArchivedException();
        }

        if (actorRole != Role.ADMIN && !course.getLeaderId().equals(actorId)) {
            throw new NotAuthorizedToArchiveCourseException();
        }

        course.archive();
        this.courseRepository.save(course);

        List<Assignment> assignments = this.assignmentRepository.findByCourseId(courseId);
        List<Assignment> activeAssignments = assignments.stream()
                .filter(assignment -> !assignment.isArchived())
                .peek(Assignment::archive)
                .toList();

        if (!activeAssignments.isEmpty()) {
            this.assignmentRepository.saveAll(activeAssignments);
        }
    }
}
