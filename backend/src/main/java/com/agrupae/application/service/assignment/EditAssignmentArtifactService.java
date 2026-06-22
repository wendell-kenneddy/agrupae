package com.agrupae.application.service.assignment;

import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

import com.agrupae.application.port.in.assignment.EditAssignmentArtifactUseCase;
import com.agrupae.application.port.in.assignment.AssignmentArtifactView;
import com.agrupae.application.port.out.assignment.AssignmentArtifactRepository;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentArtifact;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.role.Role;
import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.assignment.AssignmentArtifactNotFoundException;
import com.agrupae.application.exception.assignment.NotCourseLeaderException;
import com.agrupae.application.exception.course.CourseArchivedException;
import com.agrupae.application.exception.course.CourseNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
public class EditAssignmentArtifactService implements EditAssignmentArtifactUseCase {
    private final AssignmentArtifactRepository assignmentArtifactRepository;
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;

    @Override
    @Transactional
    public AssignmentArtifactView handle(
            @NonNull UUID actorId,
            @NonNull Role actorRole,
            @NonNull UUID courseId,
            @NonNull UUID assignmentId,
            @NonNull UUID artifactId,
            @NonNull String name,
            @NonNull String description,
            @NonNull String resourceLink,
            boolean required) {
        Course course = this.courseRepository.findById(courseId);

        if (course == null || !courseMembershipRepository.exists(actorId, courseId)) {
            throw new CourseNotFoundException();
        }

        if (course.isArchived()) {
            throw new CourseArchivedException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);

        if (assignment == null || !assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        if (actorRole != Role.ADMIN && !course.getLeaderId().equals(actorId)) {
            throw new NotCourseLeaderException("The user is not the course leader.");
        }

        AssignmentArtifact artifact = this.assignmentArtifactRepository.findById(artifactId);

        if (artifact == null || !artifact.getAssignmentId().equals(assignmentId)) {
            throw new AssignmentArtifactNotFoundException();
        }

        artifact.update(name, description, resourceLink, required);

        AssignmentArtifact saved = this.assignmentArtifactRepository.save(artifact);

        return new AssignmentArtifactView(
                saved.getId(),
                saved.getAssignmentId(),
                saved.getName(),
                saved.getDescription(),
                saved.getResourceLink(),
                saved.isRequired(),
                saved.getCreatedAt(),
                saved.getUpdatedAt());
    }
}
