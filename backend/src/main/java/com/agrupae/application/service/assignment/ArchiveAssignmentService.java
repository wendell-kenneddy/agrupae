package com.agrupae.application.service.assignment;

import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.assignment.NotAuthorizedToArchiveAssignmentException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.assignment.ArchiveAssignmentUseCase;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.role.Role;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArchiveAssignmentService implements ArchiveAssignmentUseCase {
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void handle(UUID actorId, Role actorRole, UUID courseId, UUID assignmentId) {
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
            throw new NotAuthorizedToArchiveAssignmentException();
        }

        assignment.archive();
        this.assignmentRepository.save(assignment);
    }
}
