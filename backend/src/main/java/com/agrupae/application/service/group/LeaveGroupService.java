package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.AssignmentExpiredException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.LeavingGroupNotAllowed;
import com.agrupae.application.port.in.group.LeaveGroupUseCase;
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
public class LeaveGroupService implements LeaveGroupUseCase {

    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;

    @Override
    @Transactional
    public void handle(@NonNull UUID userId, @NonNull UUID groupId, @NonNull UUID courseId, @NonNull UUID assignmentId) {
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

        if (Instant.now().isAfter(assignment.getDueDate())) {
            throw new AssignmentExpiredException();
        }

        Group group = this.groupRepository.findById(groupId);

        if (group == null || !group.getAssignmentId().equals(assignmentId)
                || !this.groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)) {
            throw new GroupNotFoundException();
        }

        if (!assignment.getAssignmentFlags().studentsCanLeaveGroups()) {
            throw new LeavingGroupNotAllowed();
        }

        boolean wasLeader = group.getLeaderId().equals(userId);

        this.groupMemberRepository.deleteByGroupIdAndMemberId(groupId, userId);

        int remainingMembers = this.groupMemberRepository.countByGroupId(groupId);

        if (remainingMembers == 0) {
            this.groupRepository.deleteById(groupId);
        } else if (wasLeader) {
            UUID newLeaderId = this.groupMemberRepository
                    .findOldestMemberIdExcluding(groupId, userId);

            group.transferLeadership(newLeaderId);
            this.groupRepository.save(group);
        }
    }
}
