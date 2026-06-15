package com.agrupae.application.service.group;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseArchivedException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupArtifactNotFoundException;
import com.agrupae.application.exception.group.GroupMemberNotFoundException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.port.in.group.ChangeGroupArtifactPrivacyUseCase;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.group.GroupArtifactRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupArtifact;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChangeGroupArtifactPrivacyService implements ChangeGroupArtifactPrivacyUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupArtifactRepository groupArtifactRepository;

    @Override
    @Transactional
    public void handle(
            @NonNull UUID courseId,
            @NonNull UUID assignmentId,
            @NonNull UUID groupId,
            @NonNull UUID artifactId,
            @NonNull UUID userId,
            boolean privateArtifact) {

        Course course = this.courseRepository.findById(courseId);

        if (course == null) {
            throw new CourseNotFoundException();
        }

        if (!this.courseMembershipRepository.exists(userId, courseId)) {
            throw new CourseNotFoundException();
        }

        if (course.isArchived()) {
            throw new CourseArchivedException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);

        if (assignment == null || !assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        if (assignment.isArchived()) {
            throw new AssignmentArchivedException();
        }

        Group group = this.groupRepository.findById(groupId);

        if (group == null || !group.getAssignmentId().equals(assignmentId)) {
            throw new GroupNotFoundException();
        }

        if (!this.groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)) {
            throw new GroupMemberNotFoundException();
        }

        GroupArtifact artifact = this.groupArtifactRepository.findById(artifactId);

        if (artifact == null || !artifact.getGroupId().equals(groupId)) {
            throw new GroupArtifactNotFoundException();
        }

        if (artifact.isPrivateArtifact() == privateArtifact) {
            return;
        }

        if (privateArtifact) {
            artifact.makePrivate();
        } else {
            artifact.makePublic();
        }

        this.groupArtifactRepository.save(artifact);
    }
}
