package com.agrupae.application.service.assignment;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.agrupae.application.port.in.assignment.AssignmentArtifactView;
import com.agrupae.domain.assignment.AssignmentArtifact;
import com.agrupae.application.port.in.assignment.GetAssignmentArtifactsUseCase;
import com.agrupae.application.port.out.assignment.AssignmentArtifactRepository;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.course.Course;
import com.agrupae.application.port.out.course.CourseRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAssignmentArtifactsService implements GetAssignmentArtifactsUseCase {
    private final AssignmentRepository assignmentRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentArtifactRepository assignmentArtifactRepository;
    private final CourseRepository courseRepository;

    @Override
    public List<AssignmentArtifactView> handle(@NonNull UUID userId, @NonNull UUID courseId, @NonNull UUID assignmentId) {
        Course course = this.courseRepository.findById(courseId);
        Assignment assignment = this.assignmentRepository.findById(assignmentId);
        
        if (course == null || !courseMembershipRepository.exists(userId, courseId)) 
            throw new CourseNotFoundException();

        if (assignment == null || !assignment.getCourseId().equals(courseId))
            throw new AssignmentNotFoundException();

        List<AssignmentArtifact> artifacts = this.assignmentArtifactRepository.findByAssignmentId(assignmentId);

        return artifacts.stream()
                .map(artifact -> new AssignmentArtifactView(
                        artifact.getId(),
                        artifact.getAssignmentId(),
                        artifact.getName(),
                        artifact.getDescription(),
                        artifact.getResourceLink(),
                        artifact.getCreatedAt(),
                        artifact.getUpdatedAt()))
                .collect(Collectors.toList());
    }
}
