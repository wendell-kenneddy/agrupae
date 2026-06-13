package com.agrupae.application.service.assignment;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.NotCourseLeaderException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.assignment.AssignmentView;
import com.agrupae.application.port.in.assignment.CreateAssignmentUseCase;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
public class CreateAssignmentService implements CreateAssignmentUseCase {
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public AssignmentView handle(
        @NonNull UUID leaderId,
        @NonNull UUID courseId,
        @NonNull String name,
        String description, 
        @NonNull Instant dueDate,
        AssignmentFlags assignmentFlags) {
            Course course = this.courseRepository.findById(courseId);

            if (course == null) {
                throw new CourseNotFoundException();
            }

            if (!course.getLeaderId().equals(leaderId)) {
                throw new NotCourseLeaderException("Only course leader can create assignments.");
            }

            Assignment assignment = Assignment.create(courseId, name, description, dueDate, assignmentFlags);

            this.assignmentRepository.save(assignment);

            AssignmentView view = new AssignmentView(
                assignment.getId(),
                assignment.getCourseId(),
                assignment.getName(),
                assignment.getDescription(),
                assignment.getAssignmentFlags(),
                assignment.isArchived(),
                assignment.getDueDate(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt());

            return view;
    }

}
