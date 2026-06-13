package com.agrupae.application.service.group;

import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupMemberNotFoundException;
import com.agrupae.application.exception.group.GroupMemberRemovalNotAllowedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.exception.group.SelfRemovalNotAllowedException;
import com.agrupae.application.port.in.group.RemoveGroupMemberUseCase;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.group.Group;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
public class RemoveGroupMemberService implements RemoveGroupMemberUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public void handle(@NonNull UUID courseId, @NonNull UUID assignmentId, @NonNull UUID groupId, @NonNull UUID userId, @NonNull UUID memberId) {
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

        Group group = this.groupRepository.findById(groupId);
        if (group == null || !group.getAssignmentId().equals(assignmentId)) {
            throw new GroupNotFoundException();
        }

        if (!group.getLeaderId().equals(userId)) {
            throw new NotGroupLeaderException();
        }

        if (!assignment.getAssignmentFlags().groupLeaderCanRemoveMembers()) {
            throw new GroupMemberRemovalNotAllowedException();
        }

        if (memberId.equals(userId)) {
            throw new SelfRemovalNotAllowedException();
        }

        if (!this.groupMemberRepository.existsByGroupIdAndMemberId(groupId, memberId)) {
            throw new GroupMemberNotFoundException();
        }

        this.groupMemberRepository.deleteByGroupIdAndMemberId(groupId, memberId);
    }
}
