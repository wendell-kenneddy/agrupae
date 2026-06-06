package com.agrupae.application.service.assignment;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.assignment.AssignmentView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetAnAssignmentServiceTest {

    private AssignmentRepository assignmentRepository;
    private CourseRepository courseRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private GetAnAssignmentService service;

    @BeforeEach
    void setUp() {
        assignmentRepository = mock(AssignmentRepository.class);
        courseRepository = mock(CourseRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        service = new GetAnAssignmentService(courseRepository, courseMembershipRepository, assignmentRepository);
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
        void withValidInputs_returnsMappedView() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Assignment assignment = buildAssignment(assignmentId, courseId);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);

            AssignmentView view = service.handle(userId, courseId, assignmentId);

            assertThat(view.id()).isEqualTo(assignmentId);
            assertThat(view.courseId()).isEqualTo(courseId);
            assertThat(view.name()).isEqualTo("First assignment");
            assertThat(view.description()).isEqualTo("Do the thing");
            assertThat(view.assignmentFlags()).isEqualTo(assignment.getAssignmentFlags());
            assertThat(view.isArchived()).isFalse();
            assertThat(view.dueDate()).isEqualTo(assignment.getDueDate());
            assertThat(view.createdAt()).isEqualTo(assignment.getCreatedAt());
            assertThat(view.updatedAt()).isEqualTo(assignment.getUpdatedAt());
        }

        @Test
        void withNonExistentCourse_throwsCourseNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(assignmentRepository.findById(assignmentId)).thenReturn(buildAssignment(assignmentId, courseId));
            when(courseRepository.findById(courseId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId))
                    .isInstanceOf(CourseNotFoundException.class);
        }

        @Test
        void withUserNotMember_throwsCourseNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(assignmentRepository.findById(assignmentId)).thenReturn(buildAssignment(assignmentId, courseId));
            when(courseRepository.findById(courseId)).thenReturn(course(courseId));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId))
                    .isInstanceOf(CourseNotFoundException.class);
        }

        @Test
        void withAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(assignmentRepository.findById(assignmentId)).thenReturn(null);
            when(courseRepository.findById(courseId)).thenReturn(course(courseId));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void withAssignmentInDifferentCourse_throwsAssignmentNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID otherCourseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(assignmentRepository.findById(assignmentId)).thenReturn(buildAssignment(assignmentId, otherCourseId));
            when(courseRepository.findById(courseId)).thenReturn(course(courseId));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        static Stream<Arguments> nullArgCases() {
            AssignmentRepository assignmentRepository = mock(AssignmentRepository.class);
            CourseRepository courseRepository = mock(CourseRepository.class);
            CourseMembershipRepository courseMembershipRepository = mock(CourseMembershipRepository.class);
            GetAnAssignmentService freshService = new GetAnAssignmentService(courseRepository, courseMembershipRepository, assignmentRepository);

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
