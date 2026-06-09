package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupMemberLimitReachedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.GroupNotClosedException;
import com.agrupae.application.exception.group.StudentAlreadyInGroupException;
import com.agrupae.application.exception.group.PendingRequestAlreadyExistsException;
import com.agrupae.application.port.in.group.GroupEntryRequestView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupEntryRequest;
import com.agrupae.domain.group.GroupEntryRequestStatus;

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

class RequestGroupEntryServiceTest {

    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private GroupMemberRepository groupMemberRepository;
    private GroupEntryRequestRepository groupEntryRequestRepository;
    private RequestGroupEntryService service;

    @BeforeEach
    void setUp() {
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        groupMemberRepository = mock(GroupMemberRepository.class);
        groupEntryRequestRepository = mock(GroupEntryRequestRepository.class);
        service = new RequestGroupEntryService(
                courseMembershipRepository, assignmentRepository,
                groupRepository, groupMemberRepository, groupEntryRequestRepository);
    }

    private static AssignmentFlags defaultFlags(int maxMembers) {
        return new AssignmentFlags(maxMembers, 10, true, true, false, false, false, false, false);
    }

    private static Assignment activeAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                defaultFlags(4), false,
                now.plusSeconds(86_400), now, now);
    }

    private static Assignment archivedAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                defaultFlags(4), true,
                now.plusSeconds(86_400), now, now);
    }

    private static Group closedGroup(UUID groupId, UUID assignmentId) {
        Instant now = Instant.now();
        return Group.reconstruct(
                groupId, assignmentId, UUID.randomUUID(), "Team Beta",
                false, true, now, now);
    }

    private static Group openGroup(UUID groupId, UUID assignmentId) {
        Instant now = Instant.now();
        return Group.reconstruct(
                groupId, assignmentId, UUID.randomUUID(), "Team Alpha",
                true, true, now, now);
    }

    @Nested
    class Handle {

        @Test
        void withValidInputs_savesAndReturnsRequest() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId));
            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, userId))
                    .thenReturn(false);
            when(groupMemberRepository.countByGroupId(groupId)).thenReturn(2);
            when(groupEntryRequestRepository.existsPendingByAssignmentIdAndUserId(assignmentId, userId))
                    .thenReturn(false);

            GroupEntryRequest savedRequest = GroupEntryRequest.create(groupId, userId);
            when(groupEntryRequestRepository.save(any(GroupEntryRequest.class))).thenReturn(savedRequest);

            GroupEntryRequestView view = service.handle(courseId, assignmentId, groupId, userId);

            assertThat(view.id()).isEqualTo(savedRequest.getId());
            assertThat(view.groupId()).isEqualTo(groupId);
            assertThat(view.userId()).isEqualTo(userId);
            assertThat(view.status()).isEqualTo(GroupEntryRequestStatus.PENDING);
            verify(groupEntryRequestRepository).save(any(GroupEntryRequest.class));
        }

        @Test
        void withUserNotEnrolled_throwsCourseNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId))
                    .isInstanceOf(CourseNotFoundException.class);
            verify(groupEntryRequestRepository, never()).save(any());
        }

        @Test
        void withAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId))
                    .isInstanceOf(AssignmentNotFoundException.class);
            verify(groupEntryRequestRepository, never()).save(any());
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
                    .thenReturn(activeAssignment(assignmentId, otherCourseId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId))
                    .isInstanceOf(AssignmentNotFoundException.class);
            verify(groupEntryRequestRepository, never()).save(any());
        }

        @Test
        void withArchivedAssignment_throwsAssignmentArchivedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(archivedAssignment(assignmentId, courseId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId))
                    .isInstanceOf(AssignmentArchivedException.class);
            verify(groupEntryRequestRepository, never()).save(any());
        }

        @Test
        void withGroupNotFound_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId))
                    .isInstanceOf(GroupNotFoundException.class);
            verify(groupEntryRequestRepository, never()).save(any());
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
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, otherAssignmentId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId))
                    .isInstanceOf(GroupNotFoundException.class);
            verify(groupEntryRequestRepository, never()).save(any());
        }

        @Test
        void withOpenGroup_throwsGroupNotClosedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(openGroup(groupId, assignmentId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId))
                    .isInstanceOf(GroupNotClosedException.class);
            verify(groupEntryRequestRepository, never()).save(any());
        }

        @Test
        void withStudentAlreadyInAGroup_throwsStudentAlreadyInGroupException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId));
            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, userId))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId))
                    .isInstanceOf(StudentAlreadyInGroupException.class);
            verify(groupEntryRequestRepository, never()).save(any());
        }

        @Test
        void withGroupFull_throwsGroupMemberLimitReachedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId));
            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, userId))
                    .thenReturn(false);
            when(groupMemberRepository.countByGroupId(groupId)).thenReturn(4);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId))
                    .isInstanceOf(GroupMemberLimitReachedException.class);
            verify(groupEntryRequestRepository, never()).save(any());
        }

        @Test
        void withPendingRequestAlreadyExists_throwsPendingRequestAlreadyExistsException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId));
            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, userId))
                    .thenReturn(false);
            when(groupMemberRepository.countByGroupId(groupId)).thenReturn(2);
            when(groupEntryRequestRepository.existsPendingByAssignmentIdAndUserId(assignmentId, userId))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId))
                    .isInstanceOf(PendingRequestAlreadyExistsException.class);
            verify(groupEntryRequestRepository, never()).save(any());
        }
    }
}
