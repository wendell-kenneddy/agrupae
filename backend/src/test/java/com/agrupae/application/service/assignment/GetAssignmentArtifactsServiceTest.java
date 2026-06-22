package com.agrupae.application.service.assignment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.assignment.AssignmentArtifactView;
import com.agrupae.application.port.out.assignment.AssignmentArtifactRepository;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.assignment.AssignmentArtifact;
import com.agrupae.domain.assignment.AssignmentFlags;

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

class GetAssignmentArtifactsServiceTest {

    private AssignmentRepository assignmentRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private CourseRepository courseRepository;
    private AssignmentArtifactRepository assignmentArtifactRepository;
    private GetAssignmentArtifactsService service;

    @BeforeEach
    void setUp() {
        assignmentRepository = mock(AssignmentRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        courseRepository = mock(CourseRepository.class);
        assignmentArtifactRepository = mock(AssignmentArtifactRepository.class);
        service = new GetAssignmentArtifactsService(
                assignmentRepository, courseMembershipRepository, assignmentArtifactRepository, courseRepository);
    }

    private static Assignment buildAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        AssignmentFlags flags = new AssignmentFlags(4, 10, true, true, false, false, false, false, false);
        return Assignment.reconstruct(assignmentId, courseId, "First assignment", "Do the thing",
                flags, false, now.plusSeconds(86_400), now, now);
    }

    private static Course course(UUID courseId) {
        Instant now = Instant.now();
        return Course.reconstruct(courseId, UUID.randomUUID(), "Algorithms", "A course",
                UUID.randomUUID().toString(), false, now, now);
    }

    @Nested
    class Handle {

        @Test
        void whenUserIsCourseMember_returnsMappedArtifacts() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Assignment assignment = buildAssignment(assignmentId, courseId);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);

            Instant now = Instant.now();
            AssignmentArtifact artifact1 = AssignmentArtifact.reconstruct(
                    UUID.randomUUID(), assignmentId, "Artifact 1", "Desc 1", "https://link1.com", true, now, now);
            AssignmentArtifact artifact2 = AssignmentArtifact.reconstruct(
                    UUID.randomUUID(), assignmentId, "Artifact 2", "Desc 2", "https://link2.com", false, now, now);
            when(assignmentArtifactRepository.findByAssignmentId(assignmentId))
                    .thenReturn(List.of(artifact1, artifact2));

            List<AssignmentArtifactView> views = service.handle(userId, courseId, assignmentId);

            assertThat(views).hasSize(2);
            
            AssignmentArtifactView view1 = views.get(0);
            assertThat(view1.id()).isEqualTo(artifact1.getId());
            assertThat(view1.assignmentId()).isEqualTo(assignmentId);
            assertThat(view1.name()).isEqualTo("Artifact 1");
            assertThat(view1.description()).isEqualTo("Desc 1");
            assertThat(view1.resourceLink()).isEqualTo("https://link1.com");
            assertThat(view1.required()).isTrue();
            assertThat(view1.createdAt()).isEqualTo(now);
            assertThat(view1.updatedAt()).isEqualTo(now);

            AssignmentArtifactView view2 = views.get(1);
            assertThat(view2.id()).isEqualTo(artifact2.getId());
            assertThat(view2.assignmentId()).isEqualTo(assignmentId);
            assertThat(view2.name()).isEqualTo("Artifact 2");
            assertThat(view2.description()).isEqualTo("Desc 2");
            assertThat(view2.resourceLink()).isEqualTo("https://link2.com");
            assertThat(view2.required()).isFalse();
            assertThat(view2.createdAt()).isEqualTo(now);
            assertThat(view2.updatedAt()).isEqualTo(now);
        }

        @Test
        void whenNoArtifactsExist_returnsEmptyList() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Assignment assignment = buildAssignment(assignmentId, courseId);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentArtifactRepository.findByAssignmentId(assignmentId)).thenReturn(List.of());

            List<AssignmentArtifactView> views = service.handle(userId, courseId, assignmentId);

            assertThat(views).isEmpty();
        }

        @Test
        void whenAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(
                    Course.reconstruct(courseId, UUID.randomUUID(), "Algorithms", "A course", UUID.randomUUID().toString(), false, Instant.now(), Instant.now()));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId))
                    .isInstanceOf(AssignmentNotFoundException.class);

            verify(assignmentArtifactRepository, never()).findByAssignmentId(any());
        }

        @Test
        void whenUserIsNotCourseMember_throwsCourseNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Assignment assignment = buildAssignment(assignmentId, courseId);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseRepository.findById(courseId)).thenReturn(com.agrupae.domain.course.Course.reconstruct(courseId, UUID.randomUUID(), "Algorithms", "A course", UUID.randomUUID().toString(), false, Instant.now(), Instant.now()));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId))
                    .isInstanceOf(CourseNotFoundException.class);

            verify(assignmentArtifactRepository, never()).findByAssignmentId(any());
        }

        static Stream<Arguments> nullArgCases() {
            AssignmentRepository assignRepo = mock(AssignmentRepository.class);
            CourseMembershipRepository membershipRepo = mock(CourseMembershipRepository.class);
            CourseRepository courseRepo = mock(CourseRepository.class);
            AssignmentArtifactRepository artifactRepo = mock(AssignmentArtifactRepository.class);
            GetAssignmentArtifactsService freshService = new GetAssignmentArtifactsService(
                    assignRepo, membershipRepo, artifactRepo, courseRepo);

            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            return Stream.of(
                    Arguments.of("UserId",
                            (ThrowingCallable) () -> freshService.handle(null, courseId, assignmentId)),
                    Arguments.of("CourseId",
                            (ThrowingCallable) () -> freshService.handle(userId, null, assignmentId)),
                    Arguments.of("AssignmentId",
                            (ThrowingCallable) () -> freshService.handle(userId, courseId, null))
            );
        }

        @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
        @MethodSource("nullArgCases")
        void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
            assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
        }
    }
}
