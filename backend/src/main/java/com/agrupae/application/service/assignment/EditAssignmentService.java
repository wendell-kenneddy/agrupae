package com.agrupae.application.service.assignment;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.assignment.NotAuthorizedToEditAssignmentException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.assignment.AssignmentView;
import com.agrupae.application.port.in.assignment.EditAssignmentUseCase;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.role.Role;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditAssignmentService implements EditAssignmentUseCase {
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public AssignmentView handle(
            final UUID actorId,
            final Role actorRole,
            final UUID courseId,
            final UUID assignmentId,
            final String name,
            final String description,
            final Instant dueDate,
            final AssignmentFlags assignmentFlags) {
        Course course = this.courseRepository.findById(courseId);

        if (course == null) {
            throw new CourseNotFoundException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);

        if (assignment == null) {
            throw new AssignmentNotFoundException();
        }

        if (!assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        if (actorRole != Role.ADMIN && !course.getLeaderId().equals(actorId)) {
            throw new NotAuthorizedToEditAssignmentException();
        }

        assignment.update(name, description, dueDate, assignmentFlags);

        this.assignmentRepository.save(assignment);

        return new AssignmentView(
                assignment.getId(),
                assignment.getCourseId(),
                assignment.getName(),
                assignment.getDescription(),
                assignment.getAssignmentFlags(),
                assignment.isArchived(),
                assignment.getDueDate(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt());
    }
}
