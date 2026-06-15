package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.agrupae.application.exception.course.CourseArchivedException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupArtifactNotFoundException;
import com.agrupae.application.exception.group.GroupMemberNotFoundException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.port.in.group.GroupArtifactView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.group.GroupArtifactRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupArtifact;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GroupArtifactServicesTest {

    private CourseMembershipRepository courseMembershipRepository;
    private CourseRepository courseRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private GroupMemberRepository groupMemberRepository;
    private GroupArtifactRepository groupArtifactRepository;

    private AddGroupArtifactService addService;
    private GetGroupArtifactsService getService;
    private EditGroupArtifactService editService;
    private DeleteGroupArtifactService deleteService;
    private ChangeGroupArtifactPrivacyService changePrivacyService;
    private GetPublicGroupArtifactsService getPublicService;

    @BeforeEach
    void setUp() {
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        courseRepository = mock(CourseRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        groupMemberRepository = mock(GroupMemberRepository.class);
        groupArtifactRepository = mock(GroupArtifactRepository.class);

        addService = new AddGroupArtifactService(
                courseMembershipRepository, courseRepository, assignmentRepository,
                groupRepository, groupMemberRepository, groupArtifactRepository);

        getService = new GetGroupArtifactsService(
                courseMembershipRepository, assignmentRepository, groupRepository,
                groupMemberRepository, groupArtifactRepository);

        editService = new EditGroupArtifactService(
                courseMembershipRepository, courseRepository, assignmentRepository,
                groupRepository, groupMemberRepository, groupArtifactRepository);

        deleteService = new DeleteGroupArtifactService(
                courseMembershipRepository, courseRepository, assignmentRepository,
                groupRepository, groupMemberRepository, groupArtifactRepository);

        changePrivacyService = new ChangeGroupArtifactPrivacyService(
                courseMembershipRepository, courseRepository, assignmentRepository,
                groupRepository, groupMemberRepository, groupArtifactRepository);

        getPublicService = new GetPublicGroupArtifactsService(
                courseMembershipRepository, assignmentRepository, groupRepository,
                groupArtifactRepository);
    }

    private static Course course(UUID id, boolean archived) {
        Instant now = Instant.now();
        return Course.reconstruct(id, UUID.randomUUID(), "Algorithms", "Desc", "INVITE", archived, now, now);
    }

    private static Assignment assignment(UUID id, UUID courseId, boolean archived) {
        Instant now = Instant.now();
        AssignmentFlags flags = new AssignmentFlags(4, 10, true, true, false, false, true, false, false);
        return Assignment.reconstruct(id, courseId, "Assignment 1", "Desc", flags, archived, now.plusSeconds(86400), now, now);
    }

    private static Group group(UUID id, UUID assignmentId) {
        Instant now = Instant.now();
        return Group.reconstruct(id, assignmentId, UUID.randomUUID(), "Team Alpha", true, true, now, now);
    }

    private static GroupArtifact artifact(UUID id, UUID groupId) {
        Instant now = Instant.now();
        return GroupArtifact.reconstruct(id, groupId, "Artifact 1", "Desc", true, "http://link.com", now, now);
    }

    private static GroupArtifact publicArtifact(UUID id, UUID groupId) {
        Instant now = Instant.now();
        return GroupArtifact.reconstruct(id, groupId, "Public Artifact", "Desc", false, "http://link.com", now, now);
    }

    @Nested
    class AddGroupArtifact {

        @Test
        void whenValid_createsAndSavesArtifact() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, false));
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);

            UUID generatedId = UUID.randomUUID();
            when(groupArtifactRepository.save(any(GroupArtifact.class))).thenAnswer(invocation -> {
                GroupArtifact a = invocation.getArgument(0);
                return GroupArtifact.reconstruct(generatedId, a.getGroupId(), a.getName(), a.getDescription(), a.isPrivateArtifact(), a.getResourceLink(), a.getCreatedAt(), a.getUpdatedAt());
            });

            GroupArtifactView view = addService.handle(userId, courseId, assignmentId, groupId, "New Artifact", "Desc", true, "http://link.com");

            assertThat(view.id()).isEqualTo(generatedId);
            assertThat(view.groupId()).isEqualTo(groupId);
            assertThat(view.name()).isEqualTo("New Artifact");
            assertThat(view.description()).isEqualTo("Desc");
            assertThat(view.privateArtifact()).isTrue();
            assertThat(view.resourceLink()).isEqualTo("http://link.com");

            verify(groupArtifactRepository).save(any(GroupArtifact.class));
        }

        @Test
        void whenDescriptionIsNull_coalescesToEmptyString() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, false));
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);

            UUID generatedId = UUID.randomUUID();
            when(groupArtifactRepository.save(any(GroupArtifact.class))).thenAnswer(invocation -> {
                GroupArtifact a = invocation.getArgument(0);
                return GroupArtifact.reconstruct(generatedId, a.getGroupId(), a.getName(), a.getDescription(), a.isPrivateArtifact(), a.getResourceLink(), a.getCreatedAt(), a.getUpdatedAt());
            });

            GroupArtifactView view = addService.handle(userId, courseId, assignmentId, groupId, "New Artifact", null, true, "http://link.com");

            assertThat(view.id()).isEqualTo(generatedId);
            assertThat(view.description()).isEqualTo("");
        }

        @Test
        void whenCourseArchived_throwsCourseArchivedException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, true));

            assertThatThrownBy(() -> addService.handle(userId, courseId, assignmentId, groupId, "New Artifact", "Desc", true, "http://link.com"))
                    .isInstanceOf(CourseArchivedException.class);
        }

        @Test
        void whenAssignmentArchived_throwsAssignmentArchivedException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, false));
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, true));

            assertThatThrownBy(() -> addService.handle(userId, courseId, assignmentId, groupId, "New Artifact", "Desc", true, "http://link.com"))
                    .isInstanceOf(AssignmentArchivedException.class);
        }

        @Test
        void whenUserNotMember_throwsGroupMemberNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, false));
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(false);

            assertThatThrownBy(() -> addService.handle(userId, courseId, assignmentId, groupId, "New Artifact", "Desc", true, "http://link.com"))
                    .isInstanceOf(GroupMemberNotFoundException.class);
        }
    }

    @Nested
    class GetGroupArtifacts {

        @Test
        void whenValid_returnsArtifacts() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID artifactId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);
            when(groupArtifactRepository.findByGroupId(groupId)).thenReturn(List.of(artifact(artifactId, groupId)));

            List<GroupArtifactView> views = getService.handle(userId, courseId, assignmentId, groupId);

            assertThat(views).hasSize(1);
            assertThat(views.get(0).id()).isEqualTo(artifactId);
        }
    }

    @Nested
    class EditGroupArtifact {

        @Test
        void whenValid_updatesArtifact() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID artifactId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, false));
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);

            GroupArtifact existing = artifact(artifactId, groupId);
            when(groupArtifactRepository.findById(artifactId)).thenReturn(existing);
            when(groupArtifactRepository.save(any(GroupArtifact.class))).thenAnswer(invocation -> invocation.getArgument(0));

            GroupArtifactView view = editService.handle(userId, courseId, assignmentId, groupId, artifactId, "Updated Name", "Updated Desc", "http://newlink.com");

            assertThat(view.name()).isEqualTo("Updated Name");
            assertThat(view.description()).isEqualTo("Updated Desc");
            assertThat(view.privateArtifact()).isTrue();
            assertThat(view.resourceLink()).isEqualTo("http://newlink.com");
        }

        @Test
        void whenDescriptionIsNull_coalescesToEmptyString() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID artifactId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, false));
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);

            GroupArtifact existing = artifact(artifactId, groupId);
            when(groupArtifactRepository.findById(artifactId)).thenReturn(existing);
            when(groupArtifactRepository.save(any(GroupArtifact.class))).thenAnswer(invocation -> invocation.getArgument(0));

            GroupArtifactView view = editService.handle(userId, courseId, assignmentId, groupId, artifactId, "Updated Name", null, "http://newlink.com");

            assertThat(view.description()).isEqualTo("");
        }

        @Test
        void whenArtifactNotFound_throwsGroupArtifactNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID artifactId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, false));
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);
            when(groupArtifactRepository.findById(artifactId)).thenReturn(null);

            assertThatThrownBy(() -> editService.handle(userId, courseId, assignmentId, groupId, artifactId, "Updated Name", "Updated Desc", "http://newlink.com"))
                    .isInstanceOf(GroupArtifactNotFoundException.class);
        }
    }

    @Nested
    class DeleteGroupArtifact {

        @Test
        void whenValid_deletesArtifact() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID artifactId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, false));
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);
            when(groupArtifactRepository.findById(artifactId)).thenReturn(artifact(artifactId, groupId));

            deleteService.handle(userId, courseId, assignmentId, groupId, artifactId);

            verify(groupArtifactRepository).deleteById(artifactId);
        }
    }

    @Nested
    class ChangeGroupArtifactPrivacy {

        @Test
        void whenValidAndChangingToPublic_updatesPrivacy() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID artifactId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, false));
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);

            GroupArtifact existing = artifact(artifactId, groupId);
            assertThat(existing.isPrivateArtifact()).isTrue();
            
            when(groupArtifactRepository.findById(artifactId)).thenReturn(existing);

            changePrivacyService.handle(courseId, assignmentId, groupId, artifactId, userId, false);

            assertThat(existing.isPrivateArtifact()).isFalse();
            verify(groupArtifactRepository).save(existing);
        }

        @Test
        void whenPrivacyIsAlreadySame_doesNotSave() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID artifactId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, false));
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);

            GroupArtifact existing = artifact(artifactId, groupId);
            assertThat(existing.isPrivateArtifact()).isTrue();
            
            when(groupArtifactRepository.findById(artifactId)).thenReturn(existing);

            changePrivacyService.handle(courseId, assignmentId, groupId, artifactId, userId, true);

            verify(groupArtifactRepository, never()).save(any(GroupArtifact.class));
        }

        @Test
        void whenArtifactNotFound_throwsGroupArtifactNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID artifactId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId, false));
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupMemberRepository.existsByGroupIdAndMemberId(groupId, userId)).thenReturn(true);
            when(groupArtifactRepository.findById(artifactId)).thenReturn(null);

            assertThatThrownBy(() -> changePrivacyService.handle(courseId, assignmentId, groupId, artifactId, userId, false))
                    .isInstanceOf(GroupArtifactNotFoundException.class);
        }
    }

    @Nested
    class GetPublicGroupArtifacts {

        @Test
        void whenValid_returnsOnlyPublicArtifacts() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID artifactId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupArtifactRepository.findPublicByGroupId(groupId)).thenReturn(List.of(publicArtifact(artifactId, groupId)));

            List<GroupArtifactView> views = getPublicService.handle(userId, courseId, assignmentId, groupId);

            assertThat(views).hasSize(1);
            assertThat(views.get(0).id()).isEqualTo(artifactId);
            assertThat(views.get(0).privateArtifact()).isFalse();
        }

        @Test
        void doesNotRequireGroupMembership() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(group(groupId, assignmentId));
            when(groupArtifactRepository.findPublicByGroupId(groupId)).thenReturn(List.of());

            List<GroupArtifactView> views = getPublicService.handle(userId, courseId, assignmentId, groupId);

            assertThat(views).isEmpty();
            verify(groupMemberRepository, never()).existsByGroupIdAndMemberId(any(), any());
        }

        @Test
        void whenNotCourseMember_throwsCourseNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> getPublicService.handle(userId, courseId, assignmentId, groupId))
                    .isInstanceOf(CourseNotFoundException.class);
        }

        @Test
        void whenGroupNotFound_throwsGroupNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment(assignmentId, courseId, false));
            when(groupRepository.findById(groupId)).thenReturn(null);

            assertThatThrownBy(() -> getPublicService.handle(userId, courseId, assignmentId, groupId))
                    .isInstanceOf(GroupNotFoundException.class);
        }
    }
}
