package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupMemberNotFoundException;
import com.agrupae.application.exception.group.GroupMemberRemovalNotAllowedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.exception.group.SelfRemovalNotAllowedException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RemoveGroupMemberServiceTest {

    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private GroupMemberRepository groupMemberRepository;
    private RemoveGroupMemberService service;

    @BeforeEach
    void setUp() {
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        groupMemberRepository = mock(GroupMemberRepository.class);
        service = new RemoveGroupMemberService(
                courseMembershipRepository, assignmentRepository,
                groupRepository, groupMemberRepository);
    }

    private static AssignmentFlags flagsWithRemoval(boolean removalAllowed) {
        return new AssignmentFlags(4, 10, true, true, false, removalAllowed, false, false, false);
    }

    private static Assignment activeAssignment(UUID assignmentId, UUID courseId, boolean removalAllowed) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flagsWithRemoval(removalAllowed), false,
                now.plusSeconds(86_400), now, now);
    }

    private static Assignment archivedAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flagsWithRemoval(true), true,
                now.plusSeconds(86_400), now, now);
    }

    private static Group group(UUID groupId, UUID assignmentId, UUID leaderId) {
        Instant now = Instant.now();
        return Group.reconstruct(groupId, assignmentId, leaderId, "Team Alpha", true, true, now, now);
    }

    @Nested
    class Handle {

        @Test
        void withValidRequest_removesMember() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, leaderId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, memberId)).thenReturn(true);

            service.handle(courseId, assignmentId, groupId, leaderId, memberId);

            verify(groupMemberRepository).deleteByGroupIdAndMemberId(groupId, memberId);
        }

        @Test
        void withUserNotEnrolled_throwsCourseNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, memberId))
                    .isInstanceOf(CourseNotFoundException.class);
            verify(groupMemberRepository, never()).deleteByGroupIdAndMemberId(any(), any());
        }

        @Test
        void withAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, memberId))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void withAssignmentFromDifferentCourse_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID otherCourseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, otherCourseId, true));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, memberId))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void withAssignmentArchived_throwsAssignmentArchivedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(archivedAssignment(assignmentId, courseId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, memberId))
                    .isInstanceOf(AssignmentArchivedException.class);
        }

        @Test
        void withGroupNotFound_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, memberId))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void withGroupFromDifferentAssignment_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID otherAssignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, otherAssignmentId, leaderId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, leaderId, memberId))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void withUserNotGroupLeader_throwsNotGroupLeaderExceptionEvenWhenRemovalNotAllowed() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID otherUserId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            when(courseMembershipRepository.exists(otherUserId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, leaderId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, otherUserId, memberId))
                    .isInstanceOf(NotGroupLeaderException.class);
        }

        @Test
        void withRemovalNotAllowed_throwsGroupMemberRemovalNotAllowedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, leaderId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, leaderId, memberId))
                    .isInstanceOf(GroupMemberRemovalNotAllowedException.class);
        }

        @Test
        void withLeaderRemovingThemselves_throwsSelfRemovalNotAllowedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, leaderId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, leaderId, leaderId))
                    .isInstanceOf(SelfRemovalNotAllowedException.class);
            verify(groupMemberRepository, never()).deleteByGroupIdAndMemberId(any(), any());
        }

        @Test
        void withTargetNotAGroupMember_throwsGroupMemberNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, leaderId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, memberId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, leaderId, memberId))
                    .isInstanceOf(GroupMemberNotFoundException.class);
            verify(groupMemberRepository, never()).deleteByGroupIdAndMemberId(any(), any());
        }
    }
}
