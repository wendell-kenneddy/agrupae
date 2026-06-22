package com.agrupae.application.service.assignment;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.assignment.NotCourseLeaderException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.assignment.AssignmentArtifactView;
import com.agrupae.application.port.out.assignment.AssignmentArtifactRepository;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentArtifact;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.exception.DomainException;
import com.agrupae.domain.role.Role;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddReferenceArtifactServiceTest {

        private AssignmentArtifactRepository assignmentArtifactRepository;
        private AssignmentRepository assignmentRepository;
        private CourseRepository courseRepository;
        private CourseMembershipRepository courseMembershipRepository;
        private AddAssignmentArtifactService service;

        @BeforeEach
        void setUp() {
                assignmentArtifactRepository = mock(AssignmentArtifactRepository.class);
                assignmentRepository = mock(AssignmentRepository.class);
                courseRepository = mock(CourseRepository.class);
                courseMembershipRepository = mock(CourseMembershipRepository.class);
                service = new AddAssignmentArtifactService(
                                assignmentArtifactRepository, assignmentRepository, courseRepository,
                                courseMembershipRepository);
        }

        private static Course buildCourse(UUID courseId, UUID leaderId) {
                Instant now = Instant.now();
                return Course.reconstruct(courseId, leaderId, "Algorithms", "A course on algorithms",
                                UUID.randomUUID().toString(), false, now, now);
        }

        private static Assignment buildAssignment(UUID assignmentId, UUID courseId) {
                Instant now = Instant.now();
                AssignmentFlags flags = new AssignmentFlags(4, 10, true, true, false, false, false, false, false);
                return Assignment.reconstruct(assignmentId, courseId, "First assignment", "Do the thing",
                                flags, false, now.plusSeconds(86_400), now, now);
        }

        @Nested
        class Handle {

                @Test
                void asLeader_persistsAndReturnsMappedView() {
                        UUID leaderId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();

                        Assignment assignment = buildAssignment(assignmentId, courseId);
                        Course course = buildCourse(courseId, leaderId);

                        when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
                        when(courseRepository.findById(courseId)).thenReturn(course);
                        when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
                        when(assignmentArtifactRepository.save(any(AssignmentArtifact.class)))
                                        .thenAnswer(inv -> inv.getArgument(0));

                        AssignmentArtifactView view = service.handle(
                                        leaderId, Role.USER, courseId, assignmentId, "Ref Article", "Read this",
                                        "https://example.com/paper");

                        verify(assignmentArtifactRepository).save(any(AssignmentArtifact.class));
                        assertThat(view.id()).isNotNull();
                        assertThat(view.assignmentId()).isEqualTo(assignmentId);
                        assertThat(view.name()).isEqualTo("Ref Article");
                        assertThat(view.description()).isEqualTo("Read this");
                        assertThat(view.resourceLink()).isEqualTo("https://example.com/paper");
                        assertThat(view.required()).isFalse();
                        assertThat(view.createdAt()).isNotNull();
                        assertThat(view.updatedAt()).isEqualTo(view.createdAt());
                }

                @Test
                void asLeaderWithRequiredTrue_persistsAndReturnsMappedViewWithRequiredTrue() {
                        UUID leaderId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();

                        Assignment assignment = buildAssignment(assignmentId, courseId);
                        Course course = buildCourse(courseId, leaderId);

                        when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
                        when(courseRepository.findById(courseId)).thenReturn(course);
                        when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
                        when(assignmentArtifactRepository.save(any(AssignmentArtifact.class)))
                                        .thenAnswer(inv -> inv.getArgument(0));

                        AssignmentArtifactView view = service.handle(
                                        leaderId, Role.USER, courseId, assignmentId, "Ref Article", "Read this",
                                        "https://example.com/paper", true);

                        verify(assignmentArtifactRepository).save(any(AssignmentArtifact.class));
                        assertThat(view.id()).isNotNull();
                        assertThat(view.assignmentId()).isEqualTo(assignmentId);
                        assertThat(view.name()).isEqualTo("Ref Article");
                        assertThat(view.description()).isEqualTo("Read this");
                        assertThat(view.resourceLink()).isEqualTo("https://example.com/paper");
                        assertThat(view.required()).isTrue();
                        assertThat(view.createdAt()).isNotNull();
                        assertThat(view.updatedAt()).isEqualTo(view.createdAt());
                }

                @Test
                void whenCourseNotFound_throwsCourseNotFoundException() {
                        UUID userId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();

                        when(courseRepository.findById(courseId)).thenReturn(null);
                        when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

                        assertThatThrownBy(() -> service.handle(
                                        userId, Role.USER, courseId, assignmentId, "Ref Article", "Read this",
                                        "https://example.com/paper"))
                                        .isInstanceOf(CourseNotFoundException.class);

                        verify(assignmentArtifactRepository, never()).save(any());
                }

                @Test
                void whenAssignmentNotFound_throwsAssignmentNotFoundException() {
                        UUID userId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();

                        when(courseRepository.findById(courseId)).thenReturn(buildCourse(courseId, UUID.randomUUID()));
                        when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
                        when(assignmentRepository.findById(assignmentId)).thenReturn(null);

                        assertThatThrownBy(() -> service.handle(
                                        userId, Role.USER, courseId, assignmentId, "Ref Article", "Read this",
                                        "https://example.com/paper"))
                                        .isInstanceOf(AssignmentNotFoundException.class);

                        verify(assignmentArtifactRepository, never()).save(any());
                }

                @Test
                void whenUserIsNotLeader_throwsNotCourseLeaderException() {
                        UUID leaderId = UUID.randomUUID();
                        UUID studentId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();

                        when(assignmentRepository.findById(assignmentId))
                                        .thenReturn(buildAssignment(assignmentId, courseId));
                        when(courseRepository.findById(courseId)).thenReturn(buildCourse(courseId, leaderId));
                        when(courseMembershipRepository.exists(studentId, courseId)).thenReturn(true);

                        assertThatThrownBy(() -> service.handle(
                                        studentId, Role.USER, courseId, assignmentId, "Ref Article", "Read this",
                                        "https://example.com/paper"))
                                        .isInstanceOf(NotCourseLeaderException.class);

                        verify(assignmentArtifactRepository, never()).save(any());
                }

                @Test
                void withBlankName_propagatesDomainException() {
                        UUID leaderId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();

                        when(assignmentRepository.findById(assignmentId))
                                        .thenReturn(buildAssignment(assignmentId, courseId));
                        when(courseRepository.findById(courseId)).thenReturn(buildCourse(courseId, leaderId));
                        when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);

                        assertThatThrownBy(() -> service.handle(
                                        leaderId, Role.USER, courseId, assignmentId, "   ", "Read this",
                                        "https://example.com/paper"))
                                        .isInstanceOf(DomainException.class)
                                        .hasMessage("Assignment artifact name cannot be blank.");

                        verify(assignmentArtifactRepository, never()).save(any());
                }

                @Test
                void withBlankResourceLink_propagatesDomainException() {
                        UUID leaderId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();

                        when(assignmentRepository.findById(assignmentId))
                                        .thenReturn(buildAssignment(assignmentId, courseId));
                        when(courseRepository.findById(courseId)).thenReturn(buildCourse(courseId, leaderId));
                        when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);

                        assertThatThrownBy(() -> service.handle(
                                        leaderId, Role.USER, courseId, assignmentId, "Ref Article", "Read this", "   "))
                                        .isInstanceOf(DomainException.class)
                                        .hasMessage("Resource link cannot be blank.");

                        verify(assignmentArtifactRepository, never()).save(any());
                }

                static Stream<Arguments> nullArgCases() {
                        AssignmentArtifactRepository artifactRepo = mock(AssignmentArtifactRepository.class);
                        AssignmentRepository assignRepo = mock(AssignmentRepository.class);
                        CourseRepository courseRepo = mock(CourseRepository.class);
                        CourseMembershipRepository membershipRepo = mock(CourseMembershipRepository.class);
                        AddAssignmentArtifactService freshService = new AddAssignmentArtifactService(artifactRepo,
                                        assignRepo, courseRepo, membershipRepo);

                        UUID userId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();

                        return Stream.of(
                                        Arguments.of("UserId",
                                                        (ThrowingCallable) () -> freshService.handle(null, Role.USER, courseId,
                                                                        assignmentId, "Name", "Desc", "http://x.com")),
                                        Arguments.of("ActorRole",
                                                        (ThrowingCallable) () -> freshService.handle(userId, null, courseId,
                                                                        assignmentId, "Name", "Desc", "http://x.com")),
                                        Arguments.of("CourseId",
                                                        (ThrowingCallable) () -> freshService.handle(userId, Role.USER, null,
                                                                        assignmentId, "Name", "Desc", "http://x.com")),
                                        Arguments.of("AssignmentId",
                                                        (ThrowingCallable) () -> freshService.handle(userId, Role.USER, courseId,
                                                                        null, "Name", "Desc", "http://x.com")),
                                        Arguments.of("Name",
                                                        (ThrowingCallable) () -> freshService.handle(userId, Role.USER, courseId,
                                                                        assignmentId, null, "Desc", "http://x.com")),
                                        Arguments.of("Description",
                                                        (ThrowingCallable) () -> freshService.handle(userId, Role.USER, courseId,
                                                                        assignmentId, "Name", null, "http://x.com")),
                                        Arguments.of("ResourceLink",
                                                        (ThrowingCallable) () -> freshService.handle(userId, Role.USER, courseId,
                                                                        assignmentId, "Name", "Desc", null)));
                }

                @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
                @MethodSource("nullArgCases")
                void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
                        assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
                }
        }
}
