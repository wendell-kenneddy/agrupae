package com.agrupae.domain.assignment;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import com.agrupae.domain.exception.DomainException;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssignmentTest {

    private static AssignmentFlags validFlags() {
        return new AssignmentFlags(4, 10, true, true, false, false, false, false, false);
    }

    @Nested
    class Create {

        @Test
        void withValidInputs_returnsPopulatedAssignment() {
            UUID courseId = UUID.randomUUID();
            Instant dueDate = Instant.now().plusSeconds(86_400);
            AssignmentFlags flags = validFlags();

            Assignment assignment = Assignment.create(courseId, "First assignment", "Do the thing", dueDate, flags);

            assertThat(assignment.getId()).isNotNull();
            assertThat(assignment.getCourseId()).isEqualTo(courseId);
            assertThat(assignment.getName()).isEqualTo("First assignment");
            assertThat(assignment.getDescription()).isEqualTo("Do the thing");
            assertThat(assignment.getAssignmentFlags()).isEqualTo(flags);
            assertThat(assignment.isArchived()).isFalse();
            assertThat(assignment.getDueDate()).isEqualTo(dueDate);
            assertThat(assignment.getCreatedAt()).isNotNull();
            assertThat(assignment.getUpdatedAt()).isEqualTo(assignment.getCreatedAt());
        }

        @ParameterizedTest
        @ValueSource(strings = {"   ", ""})
        void withBlankName_throwsDomainException(String blankName) {
            assertThatThrownBy(() -> Assignment.create(
                    UUID.randomUUID(), blankName, "Do the thing",
                    Instant.now().plusSeconds(86_400), validFlags()))
                            .isInstanceOf(DomainException.class)
                            .hasMessage("Assignment name cannot be blank.");
        }

        @Test
        void withDueDateBeforeCreation_throwsDomainException() {
            assertThatThrownBy(() -> Assignment.create(
                    UUID.randomUUID(), "First assignment", "Do the thing",
                    Instant.now().minusSeconds(3_600), validFlags()))
                            .isInstanceOf(DomainException.class)
                            .hasMessage("Due date cannot be before assignment creation timestamp.");
        }

        @Test
        void eachCall_generatesUniqueId() {
            Instant dueDate = Instant.now().plusSeconds(86_400);
            Assignment a = Assignment.create(UUID.randomUUID(), "A", "desc", dueDate, validFlags());
            Assignment b = Assignment.create(UUID.randomUUID(), "B", "desc", dueDate, validFlags());

            assertThat(a.getId()).isNotEqualTo(b.getId());
        }
    }

    @Nested
    class Reconstruct {

        @Test
        void withValidInputs_preservesAllFields() {
            UUID id = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-06-01T00:00:00Z");
            Instant dueDate = Instant.parse("2024-12-01T00:00:00Z");
            AssignmentFlags flags = validFlags();

            Assignment assignment = Assignment.reconstruct(
                    id, courseId, "First assignment", "Do the thing", flags, false, dueDate, createdAt, updatedAt);

            assertThat(assignment.getId()).isEqualTo(id);
            assertThat(assignment.getCourseId()).isEqualTo(courseId);
            assertThat(assignment.getName()).isEqualTo("First assignment");
            assertThat(assignment.getDescription()).isEqualTo("Do the thing");
            assertThat(assignment.getAssignmentFlags()).isEqualTo(flags);
            assertThat(assignment.isArchived()).isFalse();
            assertThat(assignment.getDueDate()).isEqualTo(dueDate);
            assertThat(assignment.getCreatedAt()).isEqualTo(createdAt);
            assertThat(assignment.getUpdatedAt()).isEqualTo(updatedAt);
        }

        @Test
        void withArchivedTrue_preservesArchivedFlag() {
            Instant now = Instant.parse("2024-01-01T00:00:00Z");
            Instant dueDate = Instant.parse("2024-12-01T00:00:00Z");

            Assignment assignment = Assignment.reconstruct(
                    UUID.randomUUID(), UUID.randomUUID(), "First assignment", "Do the thing",
                    validFlags(), true, dueDate, now, now);

            assertThat(assignment.isArchived()).isTrue();
        }

        @Test
        void withUpdatedAtBeforeCreatedAt_throwsDomainException() {
            Instant createdAt = Instant.parse("2024-06-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-01-01T00:00:00Z");
            Instant dueDate = Instant.parse("2024-12-01T00:00:00Z");

            assertThatThrownBy(() -> Assignment.reconstruct(
                    UUID.randomUUID(), UUID.randomUUID(), "First assignment", "Do the thing",
                    validFlags(), false, dueDate, createdAt, updatedAt))
                            .isInstanceOf(DomainException.class)
                            .hasMessage("Update timestamp cannot be before creation timestamp.");
        }

        static Stream<Arguments> nullArgCases() {
            UUID id = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            AssignmentFlags flags = validFlags();
            Instant now = Instant.parse("2024-01-01T00:00:00Z");
            Instant dueDate = Instant.parse("2024-12-01T00:00:00Z");
            return Stream.of(
                    Arguments.of("Id",              (ThrowingCallable) () -> Assignment.reconstruct(null, courseId, "Name", "Desc", flags, false, dueDate, now, now)),
                    Arguments.of("CourseId",        (ThrowingCallable) () -> Assignment.reconstruct(id, null, "Name", "Desc", flags, false, dueDate, now, now)),
                    Arguments.of("Name",            (ThrowingCallable) () -> Assignment.reconstruct(id, courseId, null, "Desc", flags, false, dueDate, now, now)),
                    Arguments.of("Description",     (ThrowingCallable) () -> Assignment.reconstruct(id, courseId, "Name", null, flags, false, dueDate, now, now)),
                    Arguments.of("AssignmentFlags", (ThrowingCallable) () -> Assignment.reconstruct(id, courseId, "Name", "Desc", null, false, dueDate, now, now)),
                    Arguments.of("DueDate",         (ThrowingCallable) () -> Assignment.reconstruct(id, courseId, "Name", "Desc", flags, false, null, now, now)),
                    Arguments.of("CreatedAt",       (ThrowingCallable) () -> Assignment.reconstruct(id, courseId, "Name", "Desc", flags, false, dueDate, null, now)),
                    Arguments.of("UpdatedAt",       (ThrowingCallable) () -> Assignment.reconstruct(id, courseId, "Name", "Desc", flags, false, dueDate, now, null))
            );
        }

        @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
        @MethodSource("nullArgCases")
        void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
            assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
        }
    }
}
