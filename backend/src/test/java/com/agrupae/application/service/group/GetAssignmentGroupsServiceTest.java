package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.group.AssignmentGroupsView;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.group.Group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetAssignmentGroupsServiceTest {

    private CourseRepository courseRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private GroupMemberRepository groupMemberRepository;
    private GetAssignmentGroupsService service;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        groupMemberRepository = mock(GroupMemberRepository.class);
        service = new GetAssignmentGroupsService(
                courseRepository, courseMembershipRepository,
                assignmentRepository, groupRepository, groupMemberRepository);
    }

    private static Course buildCourse(UUID courseId, UUID leaderId) {
        Instant now = Instant.now();
        return Course.reconstruct(courseId, leaderId, "Algorithms", "A course on algorithms",
                "INVITE123", false, now, now);
    }

    private static Assignment buildAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        AssignmentFlags flags = new AssignmentFlags(4, 10, true, true, false, false, false, false, false);
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flags, false, now.plusSeconds(86_400), now, now);
    }

    private static Group buildGroup(UUID groupId, UUID assignmentId, UUID leaderId, String name) {
        Instant now = Instant.now();
        return Group.reconstruct(groupId, assignmentId, leaderId, name, true, true, now, now);
    }

    @Nested
    class Handle {

        @Test
        void handle_withValidInputsAndUserInGroup_returnsViewWithMyGroupAndGroups() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Course course = buildCourse(courseId, UUID.randomUUID());
            Assignment assignment = buildAssignment(assignmentId, courseId);
            Group group = buildGroup(groupId, assignmentId, userId, "Team Alpha");

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(groupMemberRepository.findGroupIdByAssignmentIdAndMemberId(assignmentId, userId)).thenReturn(groupId);
            when(groupRepository.findById(groupId)).thenReturn(group);
            when(groupMemberRepository.countByGroupId(groupId)).thenReturn(3);

            Page<Group> groupsPage = new PageImpl<>(List.of(group), pageable, 1);
            when(groupRepository.findByAssignmentId(assignmentId, pageable)).thenReturn(groupsPage);

            AssignmentGroupsView view = service.handle(userId, courseId, assignmentId, pageable);

            assertThat(view.myGroup()).isNotNull();
            assertThat(view.myGroup().id()).isEqualTo(groupId);
            assertThat(view.myGroup().memberCount()).isEqualTo(3);
            assertThat(view.groups().getContent()).hasSize(1);
            assertThat(view.groups().getContent().get(0).id()).isEqualTo(groupId);
            assertThat(view.groups().getContent().get(0).memberCount()).isEqualTo(3);
        }

        @Test
        void handle_withValidInputsAndUserNotInGroup_returnsViewWithNullMyGroupAndGroups() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Course course = buildCourse(courseId, UUID.randomUUID());
            Assignment assignment = buildAssignment(assignmentId, courseId);
            Group group = buildGroup(UUID.randomUUID(), assignmentId, UUID.randomUUID(), "Team Alpha");

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(groupMemberRepository.findGroupIdByAssignmentIdAndMemberId(assignmentId, userId)).thenReturn(null);

            Page<Group> groupsPage = new PageImpl<>(List.of(group), pageable, 1);
            when(groupRepository.findByAssignmentId(assignmentId, pageable)).thenReturn(groupsPage);
            when(groupMemberRepository.countByGroupId(group.getId())).thenReturn(2);

            AssignmentGroupsView view = service.handle(userId, courseId, assignmentId, pageable);

            assertThat(view.myGroup()).isNull();
            assertThat(view.groups().getContent()).hasSize(1);
            assertThat(view.groups().getContent().get(0).id()).isEqualTo(group.getId());
            assertThat(view.groups().getContent().get(0).memberCount()).isEqualTo(2);
        }

        @Test
        void handle_withNonExistentCourse_throwsCourseNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            when(courseRepository.findById(courseId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, pageable))
                    .isInstanceOf(CourseNotFoundException.class);
        }

        @Test
        void handle_withUserNotEnrolled_throwsCourseNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Course course = buildCourse(courseId, UUID.randomUUID());
            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, pageable))
                    .isInstanceOf(CourseNotFoundException.class);
        }

        @Test
        void handle_withNonExistentAssignment_throwsAssignmentNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Course course = buildCourse(courseId, UUID.randomUUID());
            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, pageable))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void handle_withAssignmentFromDifferentCourse_throwsAssignmentNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID otherCourseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Course course = buildCourse(courseId, UUID.randomUUID());
            Assignment assignment = buildAssignment(assignmentId, otherCourseId);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, pageable))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void handle_withNullArguments_throwsNullPointerException() {
            UUID id = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            assertThatThrownBy(() -> service.handle(null, id, id, pageable))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> service.handle(id, null, id, pageable))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> service.handle(id, id, null, pageable))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> service.handle(id, id, id, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
