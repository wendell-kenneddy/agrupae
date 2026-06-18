package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseArchivedException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;
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

class EditGroupServiceTest {

    private CourseRepository courseRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private EditGroupService service;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        service = new EditGroupService(courseRepository, courseMembershipRepository, assignmentRepository, groupRepository);
    }

    private static Course activeCourse(UUID courseId, UUID leaderId) {
        Instant now = Instant.now();
        return Course.reconstruct(courseId, leaderId, "Software Engineering", "Description", "some-invite-code", false, now, now);
    }

    private static Course archivedCourse(UUID courseId, UUID leaderId) {
        Instant now = Instant.now();
        return Course.reconstruct(courseId, leaderId, "Software Engineering", "Description", "some-invite-code", true, now, now);
    }

    private static AssignmentFlags dummyFlags() {
        return new AssignmentFlags(4, 10, true, true, false, false, true, false, false);
    }

    private static Assignment activeAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                dummyFlags(), false,
                now.plusSeconds(86_400), now, now);
    }

    private static Assignment archivedAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                dummyFlags(), true,
                now.plusSeconds(86_400), now, now);
    }

    private static Group group(UUID groupId, UUID assignmentId, UUID leaderId, String name) {
        Instant now = Instant.now();
        return Group.reconstruct(groupId, assignmentId, leaderId, name, true, true, now, now);
    }

    @Nested
    class Handle {

        @Test
        void withValidRequest_editsGroupNameAndSaves() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            String newName = "New Team Name";

            when(courseRepository.findById(courseId)).thenReturn(activeCourse(courseId, leaderId));
            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId, leaderId, "Old Name"));

            service.handle(courseId, assignmentId, groupId, leaderId, newName);

            ArgumentCaptor<Group> groupCaptor = ArgumentCaptor.forClass(Group.class);
            verify(groupRepository).save(groupCaptor.capture());
            assertThat(groupCaptor.getValue().getName()).isEqualTo(newName);
        }

        @Test
        void withCourseNotFound_throwsCourseNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, "Name"))
                    .isInstanceOf(CourseNotFoundException.class);
            verify(groupRepository, never()).save(any());
        }

        @Test
        void withUserNotEnrolled_throwsCourseNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(activeCourse(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, "Name"))
                    .isInstanceOf(CourseNotFoundException.class);
            verify(groupRepository, never()).save(any());
        }

        @Test
        void withCourseArchived_throwsCourseArchivedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(archivedCourse(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, "Name"))
                    .isInstanceOf(CourseArchivedException.class);
            verify(groupRepository, never()).save(any());
        }

        @Test
        void withAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(activeCourse(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, "Name"))
                    .isInstanceOf(AssignmentNotFoundException.class);
            verify(groupRepository, never()).save(any());
        }

        @Test
        void withAssignmentFromDifferentCourse_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID otherCourseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(activeCourse(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(activeAssignment(assignmentId, otherCourseId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, "Name"))
                    .isInstanceOf(AssignmentNotFoundException.class);
            verify(groupRepository, never()).save(any());
        }

        @Test
        void withAssignmentArchived_throwsAssignmentArchivedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(activeCourse(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(archivedAssignment(assignmentId, courseId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, "Name"))
                    .isInstanceOf(AssignmentArchivedException.class);
            verify(groupRepository, never()).save(any());
        }

        @Test
        void withGroupNotFound_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(activeCourse(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, "Name"))
                    .isInstanceOf(GroupNotFoundException.class);
            verify(groupRepository, never()).save(any());
        }

        @Test
        void withGroupFromDifferentAssignment_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID otherAssignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(activeCourse(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, otherAssignmentId, userId, "Old Name"));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, "Name"))
                    .isInstanceOf(GroupNotFoundException.class);
            verify(groupRepository, never()).save(any());
        }

        @Test
        void withUserNotGroupLeader_throwsNotGroupLeaderException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID nonLeaderId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(activeCourse(courseId, leaderId));
            when(courseMembershipRepository.exists(nonLeaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId, leaderId, "Old Name"));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, nonLeaderId, "New Name"))
                    .isInstanceOf(NotGroupLeaderException.class);
            verify(groupRepository, never()).save(any());
        }
    }
}
