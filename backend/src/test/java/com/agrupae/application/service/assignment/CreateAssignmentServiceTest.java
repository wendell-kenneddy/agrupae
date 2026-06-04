package com.agrupae.application.service.assignment;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import com.agrupae.application.exception.assignment.NotCourseLeaderException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.assignment.AssignmentView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.exception.DomainException;

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

class CreateAssignmentServiceTest {

    private AssignmentRepository assignmentRepository;
    private CourseRepository courseRepository;
    private CreateAssignmentService service;

    @BeforeEach
    void setUp() {
        assignmentRepository = mock(AssignmentRepository.class);
        courseRepository = mock(CourseRepository.class);
        service = new CreateAssignmentService(assignmentRepository, courseRepository);
    }

    private static AssignmentFlags validFlags() {
        return new AssignmentFlags(4, 10, true, true, false, false, false, false, false);
    }

    private static Course leaderCourse(UUID courseId, UUID leaderId) {
        Instant now = Instant.now();
        return Course.reconstruct(courseId, leaderId, "Algorithms", "A course on algorithms",
                UUID.randomUUID().toString(), false, now, now);
    }

    @Nested
    class Handle {

        @Test
        void withValidInputs_persistsAndReturnsMappedView() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Instant dueDate = Instant.now().plusSeconds(86_400);
            AssignmentFlags flags = validFlags();

            when(courseRepository.findById(courseId)).thenReturn(leaderCourse(courseId, leaderId));

            AssignmentView view = service.handle(
                    leaderId, courseId, "First assignment", "Do the thing", dueDate, flags);

            verify(assignmentRepository).save(any(Assignment.class));
            assertThat(view.id()).isNotNull();
            assertThat(view.courseId()).isEqualTo(courseId);
            assertThat(view.name()).isEqualTo("First assignment");
            assertThat(view.description()).isEqualTo("Do the thing");
            assertThat(view.assignmentFlags()).isEqualTo(flags);
            assertThat(view.isArchived()).isFalse();
            assertThat(view.dueDate()).isEqualTo(dueDate);
            assertThat(view.createdAt()).isNotNull();
            assertThat(view.updatedAt()).isEqualTo(view.createdAt());
        }

        @Test
        void withNonExistentCourse_throwsCourseNotFoundException() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(
                    leaderId, courseId, "First assignment", "Do the thing",
                    Instant.now().plusSeconds(86_400), validFlags()))
                            .isInstanceOf(CourseNotFoundException.class);

            verify(assignmentRepository, never()).save(any());
        }

        @Test
        void withNonLeaderUser_throwsNotCourseLeaderException() {
            UUID leaderId = UUID.randomUUID();
            UUID otherUserId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(leaderCourse(courseId, leaderId));

            assertThatThrownBy(() -> service.handle(
                    otherUserId, courseId, "First assignment", "Do the thing",
                    Instant.now().plusSeconds(86_400), validFlags()))
                            .isInstanceOf(NotCourseLeaderException.class)
                            .hasMessage("Only course leader can create assignments.");

            verify(assignmentRepository, never()).save(any());
        }

        @Test
        void withBlankName_propagatesDomainException() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(leaderCourse(courseId, leaderId));

            assertThatThrownBy(() -> service.handle(
                    leaderId, courseId, "   ", "Do the thing",
                    Instant.now().plusSeconds(86_400), validFlags()))
                            .isInstanceOf(DomainException.class)
                            .hasMessage("Assignment name cannot be blank.");

            verify(assignmentRepository, never()).save(any());
        }

        @Test
        void withDueDateBeforeNow_propagatesDomainException() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(leaderCourse(courseId, leaderId));

            assertThatThrownBy(() -> service.handle(
                    leaderId, courseId, "First assignment", "Do the thing",
                    Instant.now().minusSeconds(3_600), validFlags()))
                            .isInstanceOf(DomainException.class)
                            .hasMessage("Due date cannot be before assignment creation timestamp.");

            verify(assignmentRepository, never()).save(any());
        }

        @Test
        void withNullCourseId_throwsCourseNotFoundException() {
            UUID leaderId = UUID.randomUUID();

            when(courseRepository.findById(null)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(
                    leaderId, null, "First assignment", "Do the thing",
                    Instant.now().plusSeconds(86_400), validFlags()))
                            .isInstanceOf(CourseNotFoundException.class);

            verify(assignmentRepository, never()).save(any());
        }

        static Stream<Arguments> nullArgCases() {
            AssignmentRepository assignmentRepository = mock(AssignmentRepository.class);
            CourseRepository courseRepository = mock(CourseRepository.class);
            CreateAssignmentService freshService =
                    new CreateAssignmentService(assignmentRepository, courseRepository);

            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Instant dueDate = Instant.now().plusSeconds(86_400);
            AssignmentFlags flags = validFlags();

            when(courseRepository.findById(courseId)).thenReturn(leaderCourse(courseId, leaderId));

            return Stream.of(
                    Arguments.of("Name",            (ThrowingCallable) () -> freshService.handle(leaderId, courseId, null, "Desc", dueDate, flags)),
                    Arguments.of("Description",     (ThrowingCallable) () -> freshService.handle(leaderId, courseId, "Name", null, dueDate, flags)),
                    Arguments.of("DueDate",         (ThrowingCallable) () -> freshService.handle(leaderId, courseId, "Name", "Desc", null, flags)),
                    Arguments.of("AssignmentFlags", (ThrowingCallable) () -> freshService.handle(leaderId, courseId, "Name", "Desc", dueDate, null))
            );
        }

        @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
        @MethodSource("nullArgCases")
        void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
            assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
        }
    }
}
