package com.agrupae.application.service.group;

import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupCreationNotAllowedException;
import com.agrupae.application.exception.group.MaxGroupsReachedException;
import com.agrupae.application.exception.group.StudentAlreadyInGroupException;
import com.agrupae.application.port.in.group.CreateGroupUseCase;
import com.agrupae.application.port.in.group.GroupView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupMember;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
public class CreateGroupService implements CreateGroupUseCase {
    private final AssignmentRepository assignmentRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public GroupView handle(@NonNull UUID userId, @NonNull UUID courseId, @NonNull UUID assignmentId, @NonNull String name, boolean open) {
        if (!this.courseMembershipRepository.exists(userId, courseId)) {
            throw new CourseNotFoundException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);

        if (assignment == null || !assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        if (assignment.isArchived()) {
            throw new AssignmentArchivedException();
        }

        if (!assignment.getAssignmentFlags().studentsCanCreateGroups()) {
            throw new GroupCreationNotAllowedException();
        }

        if (this.groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, userId)) {
            throw new StudentAlreadyInGroupException();
        }

        int currentGroupCount = this.groupRepository.countByAssignmentId(assignmentId);
        
        if (currentGroupCount >= assignment.getAssignmentFlags().maxGroups()) {
            throw new MaxGroupsReachedException();
        }

        Group group = Group.create(assignmentId, userId, name, open, true);
        this.groupRepository.save(group);
        this.groupMemberRepository.save(new GroupMember(group.getId(), userId));

        return new GroupView(
                group.getId(),
                group.getAssignmentId(),
                group.getLeaderId(),
                group.getName(),
                group.isOpen(),
                group.isMembersCanEditArtifacts(),
                group.getCreatedAt(),
                group.getUpdatedAt());
    }
}
