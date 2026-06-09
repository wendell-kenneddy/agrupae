package com.agrupae.application.service.group;

import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupMemberLimitReachedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.GroupNotOpenException;
import com.agrupae.application.exception.group.StudentAlreadyInGroupException;
import com.agrupae.application.port.in.group.JoinOpenGroupUseCase;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupMember;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JoinOpenGroupService implements JoinOpenGroupUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public void handle(UUID courseId, UUID assignmentId, UUID groupId, UUID userId) {
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

        if (!group.isOpen()) {
            throw new GroupNotOpenException();
        }

        if (this.groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, userId)) {
            throw new StudentAlreadyInGroupException();
        }

        int memberCount = this.groupMemberRepository.countByGroupId(groupId);

        if (memberCount >= assignment.getAssignmentFlags().maxGroupMembers()) {
            throw new GroupMemberLimitReachedException();
        }

        this.groupMemberRepository.save(new GroupMember(groupId, userId));
    }
}
