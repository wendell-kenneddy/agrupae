package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupModeChangeNotAllowedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.group.Group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChangeGroupModeServiceTest {

    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private GroupEntryRequestRepository groupEntryRequestRepository;
    private ChangeGroupModeService service;

    @BeforeEach
    void setUp() {
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        groupEntryRequestRepository = mock(GroupEntryRequestRepository.class);
        service = new ChangeGroupModeService(
                courseMembershipRepository, assignmentRepository,
                groupRepository, groupEntryRequestRepository);
    }

    private static AssignmentFlags flagsWithModeChange(boolean changeAllowed) {
        return new AssignmentFlags(4, 10, true, true, false, false, changeAllowed, false, false);
    }

    private static Assignment activeAssignment(UUID assignmentId, UUID courseId, boolean changeAllowed) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flagsWithModeChange(changeAllowed), false,
                now.plusSeconds(86_400), now, now);
    }

    private static Assignment archivedAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flagsWithModeChange(true), true,
                now.plusSeconds(86_400), now, now);
    }

    private static Group group(UUID groupId, UUID assignmentId, UUID leaderId, boolean open) {
        Instant now = Instant.now();
        return Group.reconstruct(groupId, assignmentId, leaderId, "Team Alpha", open, true, now, now);
    }

    @Nested
    class Handle {

        @Test
        void withDifferentMode_changesModeAndDeletesPendingRequests() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, leaderId, false));

            service.handle(courseId, assignmentId, groupId, leaderId, true);

            ArgumentCaptor<Group> groupCaptor = ArgumentCaptor.forClass(Group.class);
            verify(groupRepository).save(groupCaptor.capture());
            assertThat(groupCaptor.getValue().isOpen()).isTrue();
            verify(groupEntryRequestRepository).deleteAllPendingByGroupId(groupId);
        }

        @Test
        void withSameMode_doesNothing() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, leaderId, true));

            service.handle(courseId, assignmentId, groupId, leaderId, true);

            verify(groupRepository, never()).save(any());
            verify(groupEntryRequestRepository, never()).deleteAllPendingByGroupId(any());
        }

        @Test
        void withUserNotEnrolled_throwsCourseNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, true))
                    .isInstanceOf(CourseNotFoundException.class);
            verify(groupRepository, never()).save(any());
        }

        @Test
        void withAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, true))
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

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, true))
                    .isInstanceOf(AssignmentArchivedException.class);
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

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, true))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void withGroupFromDifferentAssignment_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID otherAssignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, true));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, otherAssignmentId, leaderId, false));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, leaderId, true))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void withUserNotGroupLeader_throwsNotGroupLeaderExceptionEvenWhenChangeNotAllowed() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID otherUserId = UUID.randomUUID();

            when(courseMembershipRepository.exists(otherUserId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, leaderId, false));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, otherUserId, true))
                    .isInstanceOf(NotGroupLeaderException.class);
        }

        @Test
        void withModeChangeNotAllowed_throwsGroupModeChangeNotAllowedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, leaderId, false));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, leaderId, true))
                    .isInstanceOf(GroupModeChangeNotAllowedException.class);
        }
    }
}
