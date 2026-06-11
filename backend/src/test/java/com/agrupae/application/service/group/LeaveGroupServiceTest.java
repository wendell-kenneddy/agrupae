package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.AssignmentExpiredException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.LeavingGroupNotAllowed;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.group.Group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LeaveGroupServiceTest {

    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private GroupMemberRepository groupMemberRepository;
    private LeaveGroupService service;

    @BeforeEach
    void setUp() {
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        groupMemberRepository = mock(GroupMemberRepository.class);
        service = new LeaveGroupService(
                groupMemberRepository, groupRepository,
                courseMembershipRepository, assignmentRepository);
    }

    private static AssignmentFlags flagsWithLeave(boolean leaveAllowed) {
        return new AssignmentFlags(4, 10, true, leaveAllowed, false, false, false, false, false);
    }

    private static Assignment activeAssignment(UUID assignmentId, UUID courseId, boolean leaveAllowed) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flagsWithLeave(leaveAllowed), false,
                now.plusSeconds(86_400), now, now);
    }

    private static Assignment expiredAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        Instant created = now.minusSeconds(172_800);
        Instant due = now.minusSeconds(86_400);
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flagsWithLeave(true), false,
                due, created, created);
    }

    private static Assignment archivedAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flagsWithLeave(true), true,
                now.plusSeconds(86_400), now, now);
    }

    private static Group group(UUID groupId, UUID assignmentId, UUID leaderId) {
        Instant now = Instant.now();
        return Group.reconstruct(groupId, assignmentId, leaderId, "Team Alpha", true, true, now, now);
    }

    @Nested
    class Handle {

        @Test
        void withUserNotEnrolled_throwsCourseNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(userId, groupId, courseId, assignmentId))
                    .isInstanceOf(CourseNotFoundException.class);
            verify(groupMemberRepository, never()).deleteByGroupIdAndMemberId(any(), any());
        }

        @Test
        void withAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId, groupId, courseId, assignmentId))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void withAssignmentFromDifferentCourse_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID otherCourseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, otherCourseId, true));

            assertThatThrownBy(() -> service.handle(userId, groupId, courseId, assignmentId))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void withAssignmentArchived_throwsAssignmentArchivedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(archivedAssignment(assignmentId, courseId));

            assertThatThrownBy(() -> service.handle(userId, groupId, courseId, assignmentId))
                    .isInstanceOf(AssignmentArchivedException.class);
        }

        @Test
        void withAssignmentExpired_throwsAssignmentExpiredException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(expiredAssignment(assignmentId, courseId));

            assertThatThrownBy(() -> service.handle(userId, groupId, courseId, assignmentId))
                    .isInstanceOf(AssignmentExpiredException.class);
        }

        @Test
        void withGroupNotFound_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId, groupId, courseId, assignmentId))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void withGroupFromDifferentAssignment_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID otherAssignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, otherAssignmentId, userId));

            assertThatThrownBy(() -> service.handle(userId, groupId, courseId, assignmentId))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void withUserNotGroupMember_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, UUID.randomUUID()));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(userId, groupId, courseId, assignmentId))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void withLeavingNotAllowed_throwsLeavingGroupNotAllowed() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, userId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(userId, groupId, courseId, assignmentId))
                    .isInstanceOf(LeavingGroupNotAllowed.class);
        }

        @Test
        void withValidRequest_andRemainingMembers_deletesMemberAndUpdatesLeader() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID newLeaderId = UUID.randomUUID();

            Group group = group(groupId, assignmentId, userId);

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId)).thenReturn(group);
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);
            when(groupMemberRepository.countByGroupId(groupId)).thenReturn(2);
            when(groupMemberRepository.findOldestMemberIdExcluding(groupId, userId)).thenReturn(newLeaderId);

            service.handle(userId, groupId, courseId, assignmentId);

            verify(groupMemberRepository).deleteByGroupIdAndMemberId(groupId, userId);
            verify(groupMemberRepository).findOldestMemberIdExcluding(groupId, userId);
            assertThat(group.getLeaderId()).isEqualTo(newLeaderId);
            verify(groupRepository).save(group);
        }

        @Test
        void withValidRequest_andNoRemainingMembers_deletesGroup() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            Group group = group(groupId, assignmentId, userId);

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId)).thenReturn(group);
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);
            when(groupMemberRepository.countByGroupId(groupId)).thenReturn(0);

            service.handle(userId, groupId, courseId, assignmentId);

            verify(groupMemberRepository).deleteByGroupIdAndMemberId(groupId, userId);
            verify(groupRepository).deleteById(groupId);
            verify(groupRepository, never()).save(any());
        }

        @Test
        void withValidRequest_andNotLeader_deletesMemberWithoutLeadershipChange() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            Group group = group(groupId, assignmentId, leaderId);

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId)).thenReturn(group);
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);
            when(groupMemberRepository.countByGroupId(groupId)).thenReturn(1);

            service.handle(userId, groupId, courseId, assignmentId);

            verify(groupMemberRepository).deleteByGroupIdAndMemberId(groupId, userId);
            verify(groupRepository, never()).deleteById(groupId);
            verify(groupRepository, never()).save(any());
            assertThat(group.getLeaderId()).isEqualTo(leaderId);
        }
    }
}
