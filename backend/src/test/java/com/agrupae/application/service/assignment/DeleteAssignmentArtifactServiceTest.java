package com.agrupae.application.service.assignment;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.assignment.AssignmentArtifactNotFoundException;
import com.agrupae.application.exception.assignment.NotCourseLeaderException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.out.assignment.AssignmentArtifactRepository;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentArtifact;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.role.Role;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteAssignmentArtifactServiceTest {

        private AssignmentArtifactRepository assignmentArtifactRepository;
        private AssignmentRepository assignmentRepository;
        private CourseRepository courseRepository;
        private CourseMembershipRepository courseMembershipRepository;
        private DeleteAssignmentArtifactService service;

        @BeforeEach
        void setUp() {
                assignmentArtifactRepository = mock(AssignmentArtifactRepository.class);
                assignmentRepository = mock(AssignmentRepository.class);
                courseRepository = mock(CourseRepository.class);
                courseMembershipRepository = mock(CourseMembershipRepository.class);
                service = new DeleteAssignmentArtifactService(
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

        private static AssignmentArtifact buildArtifact(UUID artifactId, UUID assignmentId) {
                Instant now = Instant.now();
                return AssignmentArtifact.reconstruct(artifactId, assignmentId, "Original Name", "Original Desc",
                                "https://example.com/original", now, now);
        }

        @Nested
        class Handle {

                @Test
                void asLeader_deletesArtifact() {
                        UUID leaderId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();
                        UUID artifactId = UUID.randomUUID();

                        Assignment assignment = buildAssignment(assignmentId, courseId);
                        Course course = buildCourse(courseId, leaderId);
                        AssignmentArtifact artifact = buildArtifact(artifactId, assignmentId);

                        when(courseRepository.findById(courseId)).thenReturn(course);
                        when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
                        when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
                        when(assignmentArtifactRepository.findById(artifactId)).thenReturn(artifact);

                        service.handle(leaderId, Role.USER, courseId, assignmentId, artifactId);

                        verify(assignmentArtifactRepository).delete(artifactId);
                }

                @Test
                void asAdmin_deletesArtifact() {
                        UUID leaderId = UUID.randomUUID();
                        UUID adminId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();
                        UUID artifactId = UUID.randomUUID();

                        Assignment assignment = buildAssignment(assignmentId, courseId);
                        Course course = buildCourse(courseId, leaderId);
                        AssignmentArtifact artifact = buildArtifact(artifactId, assignmentId);

                        when(courseRepository.findById(courseId)).thenReturn(course);
                        when(courseMembershipRepository.exists(adminId, courseId)).thenReturn(true);
                        when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
                        when(assignmentArtifactRepository.findById(artifactId)).thenReturn(artifact);

                        service.handle(adminId, Role.ADMIN, courseId, assignmentId, artifactId);

                        verify(assignmentArtifactRepository).delete(artifactId);
                }

                @Test
                void whenCourseNotFound_throwsCourseNotFoundException() {
                        UUID userId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();
                        UUID artifactId = UUID.randomUUID();

                        when(courseRepository.findById(courseId)).thenReturn(null);

                        assertThatThrownBy(() -> service.handle(
                                        userId, Role.USER, courseId, assignmentId, artifactId))
                                        .isInstanceOf(CourseNotFoundException.class);

                        verify(assignmentArtifactRepository, never()).delete(any());
                }

                @Test
                void whenNotMemberOfCourse_throwsCourseNotFoundException() {
                        UUID userId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();
                        UUID artifactId = UUID.randomUUID();

                        when(courseRepository.findById(courseId)).thenReturn(buildCourse(courseId, UUID.randomUUID()));
                        when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

                        assertThatThrownBy(() -> service.handle(
                                        userId, Role.USER, courseId, assignmentId, artifactId))
                                        .isInstanceOf(CourseNotFoundException.class);

                        verify(assignmentArtifactRepository, never()).delete(any());
                }

                @Test
                void whenAssignmentNotFound_throwsAssignmentNotFoundException() {
                        UUID leaderId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();
                        UUID artifactId = UUID.randomUUID();

                        when(courseRepository.findById(courseId)).thenReturn(buildCourse(courseId, leaderId));
                        when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
                        when(assignmentRepository.findById(assignmentId)).thenReturn(null);

                        assertThatThrownBy(() -> service.handle(
                                        leaderId, Role.USER, courseId, assignmentId, artifactId))
                                        .isInstanceOf(AssignmentNotFoundException.class);

                        verify(assignmentArtifactRepository, never()).delete(any());
                }

                @Test
                void whenAssignmentBelongsToAnotherCourse_throwsAssignmentNotFoundException() {
                        UUID leaderId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID anotherCourseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();
                        UUID artifactId = UUID.randomUUID();

                        when(courseRepository.findById(courseId)).thenReturn(buildCourse(courseId, leaderId));
                        when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
                        when(assignmentRepository.findById(assignmentId)).thenReturn(buildAssignment(assignmentId, anotherCourseId));

                        assertThatThrownBy(() -> service.handle(
                                        leaderId, Role.USER, courseId, assignmentId, artifactId))
                                        .isInstanceOf(AssignmentNotFoundException.class);

                        verify(assignmentArtifactRepository, never()).delete(any());
                }

                @Test
                void whenUserIsNotLeaderOrAdmin_throwsNotCourseLeaderException() {
                        UUID leaderId = UUID.randomUUID();
                        UUID studentId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();
                        UUID artifactId = UUID.randomUUID();

                        when(courseRepository.findById(courseId)).thenReturn(buildCourse(courseId, leaderId));
                        when(courseMembershipRepository.exists(studentId, courseId)).thenReturn(true);
                        when(assignmentRepository.findById(assignmentId)).thenReturn(buildAssignment(assignmentId, courseId));

                        assertThatThrownBy(() -> service.handle(
                                        studentId, Role.USER, courseId, assignmentId, artifactId))
                                        .isInstanceOf(NotCourseLeaderException.class);

                        verify(assignmentArtifactRepository, never()).delete(any());
                }

                @Test
                void whenArtifactNotFound_throwsAssignmentArtifactNotFoundException() {
                        UUID leaderId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();
                        UUID artifactId = UUID.randomUUID();

                        when(courseRepository.findById(courseId)).thenReturn(buildCourse(courseId, leaderId));
                        when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
                        when(assignmentRepository.findById(assignmentId)).thenReturn(buildAssignment(assignmentId, courseId));
                        when(assignmentArtifactRepository.findById(artifactId)).thenReturn(null);

                        assertThatThrownBy(() -> service.handle(
                                        leaderId, Role.USER, courseId, assignmentId, artifactId))
                                        .isInstanceOf(AssignmentArtifactNotFoundException.class);

                        verify(assignmentArtifactRepository, never()).delete(any());
                }

                @Test
                void whenArtifactBelongsToAnotherAssignment_throwsAssignmentArtifactNotFoundException() {
                        UUID leaderId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();
                        UUID anotherAssignmentId = UUID.randomUUID();
                        UUID artifactId = UUID.randomUUID();

                        when(courseRepository.findById(courseId)).thenReturn(buildCourse(courseId, leaderId));
                        when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
                        when(assignmentRepository.findById(assignmentId)).thenReturn(buildAssignment(assignmentId, courseId));
                        when(assignmentArtifactRepository.findById(artifactId)).thenReturn(buildArtifact(artifactId, anotherAssignmentId));

                        assertThatThrownBy(() -> service.handle(
                                        leaderId, Role.USER, courseId, assignmentId, artifactId))
                                        .isInstanceOf(AssignmentArtifactNotFoundException.class);

                        verify(assignmentArtifactRepository, never()).delete(any());
                }

                static Stream<Arguments> nullArgCases() {
                        AssignmentArtifactRepository artifactRepo = mock(AssignmentArtifactRepository.class);
                        AssignmentRepository assignRepo = mock(AssignmentRepository.class);
                        CourseRepository courseRepo = mock(CourseRepository.class);
                        CourseMembershipRepository membershipRepo = mock(CourseMembershipRepository.class);
                        DeleteAssignmentArtifactService freshService = new DeleteAssignmentArtifactService(artifactRepo,
                                        assignRepo, courseRepo, membershipRepo);

                        UUID actorId = UUID.randomUUID();
                        UUID courseId = UUID.randomUUID();
                        UUID assignmentId = UUID.randomUUID();
                        UUID artifactId = UUID.randomUUID();

                        return Stream.of(
                                        Arguments.of("ActorId",
                                                         (ThrowingCallable) () -> freshService.handle(null, Role.USER, courseId,
                                                                         assignmentId, artifactId)),
                                        Arguments.of("ActorRole",
                                                         (ThrowingCallable) () -> freshService.handle(actorId, null, courseId,
                                                                         assignmentId, artifactId)),
                                        Arguments.of("CourseId",
                                                         (ThrowingCallable) () -> freshService.handle(actorId, Role.USER, null,
                                                                         assignmentId, artifactId)),
                                        Arguments.of("AssignmentId",
                                                         (ThrowingCallable) () -> freshService.handle(actorId, Role.USER, courseId,
                                                                         null, artifactId)),
                                        Arguments.of("ArtifactId",
                                                         (ThrowingCallable) () -> freshService.handle(actorId, Role.USER, courseId,
                                                                         assignmentId, null)));
                }

                @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
                @MethodSource("nullArgCases")
                void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
                        assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
                }
        }
}
