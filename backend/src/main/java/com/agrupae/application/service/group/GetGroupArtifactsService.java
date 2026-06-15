package com.agrupae.application.service.group;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.GroupMemberNotFoundException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.port.in.group.GetGroupArtifactsUseCase;
import com.agrupae.application.port.in.group.GroupArtifactView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupArtifactRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.group.Group;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetGroupArtifactsService implements GetGroupArtifactsUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupArtifactRepository groupArtifactRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GroupArtifactView> handle(
            @NonNull UUID userId,
            @NonNull UUID courseId,
            @NonNull UUID assignmentId,
            @NonNull UUID groupId) {

        if (!this.courseMembershipRepository.exists(userId, courseId)) {
            throw new CourseNotFoundException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);

        if (assignment == null || !assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        Group group = this.groupRepository.findById(groupId);

        if (group == null || !group.getAssignmentId().equals(assignmentId)) {
            throw new GroupNotFoundException();
        }

        if (!this.groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)) {
            throw new GroupMemberNotFoundException();
        }

        return this.groupArtifactRepository.findByGroupId(groupId).stream()
                .map(artifact -> new GroupArtifactView(
                        artifact.getId(),
                        artifact.getGroupId(),
                        artifact.getName(),
                        artifact.getDescription(),
                        artifact.isPrivateArtifact(),
                        artifact.getResourceLink(),
                        artifact.getCreatedAt(),
                        artifact.getUpdatedAt()))
                .toList();
    }
}
