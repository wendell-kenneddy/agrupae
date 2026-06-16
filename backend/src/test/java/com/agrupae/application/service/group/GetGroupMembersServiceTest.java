package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.port.in.group.GroupMemberView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.role.Role;
import com.agrupae.domain.user.User;

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

class GetGroupMembersServiceTest {

    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private GroupMemberRepository groupMemberRepository;
    private UserRepository userRepository;
    private GetGroupMembersService service;

    @BeforeEach
    void setUp() {
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        groupMemberRepository = mock(GroupMemberRepository.class);
        userRepository = mock(UserRepository.class);
        service = new GetGroupMembersService(
                courseMembershipRepository,
                assignmentRepository,
                groupRepository,
                groupMemberRepository,
                userRepository);
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

    private static User buildUser(UUID userId, String name, String email) {
        Instant now = Instant.now();
        return User.reconstruct(userId, name, email, "hash", Role.USER, now, now);
    }

    @Nested
    class Handle {

        @Test
        void handle_withValidInputs_returnsPaginatedMembers() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID otherMemberId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Assignment assignment = buildAssignment(assignmentId, courseId);
            Group group = buildGroup(groupId, assignmentId, leaderId, "Team Alpha");
            User leader = buildUser(leaderId, "Alice", "alice@example.com");
            User otherMember = buildUser(otherMemberId, "Bob", "bob@example.com");

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(groupRepository.findById(groupId)).thenReturn(group);

            List<UUID> memberIds = List.of(leaderId, otherMemberId);
            when(groupMemberRepository.findMemberIdsByGroupId(groupId)).thenReturn(memberIds);

            List<User> users = List.of(leader, otherMember);
            Page<User> userPage = new PageImpl<>(users, pageable, 2);
            when(userRepository.findAllByIdIn(memberIds, pageable)).thenReturn(userPage);

            Page<GroupMemberView> result = service.handle(userId, courseId, assignmentId, groupId, pageable);

            assertThat(result.getContent()).hasSize(2);
            GroupMemberView view1 = result.getContent().get(0);
            assertThat(view1.id()).isEqualTo(leaderId);
            assertThat(view1.name()).isEqualTo("Alice");
            assertThat(view1.email()).isEqualTo("alice@example.com");
            assertThat(view1.isLeader()).isTrue();

            GroupMemberView view2 = result.getContent().get(1);
            assertThat(view2.id()).isEqualTo(otherMemberId);
            assertThat(view2.name()).isEqualTo("Bob");
            assertThat(view2.email()).isEqualTo("bob@example.com");
            assertThat(view2.isLeader()).isFalse();
        }

        @Test
        void handle_withNoMembers_returnsEmptyPage() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Assignment assignment = buildAssignment(assignmentId, courseId);
            Group group = buildGroup(groupId, assignmentId, UUID.randomUUID(), "Team Alpha");

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(groupRepository.findById(groupId)).thenReturn(group);
            when(groupMemberRepository.findMemberIdsByGroupId(groupId)).thenReturn(Collections.emptyList());

            Page<GroupMemberView> result = service.handle(userId, courseId, assignmentId, groupId, pageable);

            assertThat(result.getContent()).isEmpty();
        }

        @Test
        void handle_withUserNotCourseMember_throwsCourseNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, groupId, pageable))
                    .isInstanceOf(CourseNotFoundException.class);
        }

        @Test
        void handle_withNonExistentAssignment_throwsAssignmentNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, groupId, pageable))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void handle_withAssignmentFromDifferentCourse_throwsAssignmentNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID otherCourseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Assignment assignment = buildAssignment(assignmentId, otherCourseId);

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, groupId, pageable))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void handle_withNonExistentGroup_throwsGroupNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Assignment assignment = buildAssignment(assignmentId, courseId);

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(groupRepository.findById(groupId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, groupId, pageable))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void handle_withGroupFromDifferentAssignment_throwsGroupNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID otherAssignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Assignment assignment = buildAssignment(assignmentId, courseId);
            Group group = buildGroup(groupId, otherAssignmentId, UUID.randomUUID(), "Team Alpha");

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(groupRepository.findById(groupId)).thenReturn(group);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, groupId, pageable))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void handle_withNullArguments_throwsNullPointerException() {
            UUID id = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            assertThatThrownBy(() -> service.handle(null, id, id, id, pageable))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> service.handle(id, null, id, id, pageable))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> service.handle(id, id, null, id, pageable))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> service.handle(id, id, id, null, pageable))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> service.handle(id, id, id, id, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
