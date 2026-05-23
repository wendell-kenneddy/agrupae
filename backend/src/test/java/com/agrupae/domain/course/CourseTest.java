package com.agrupae.domain.course;

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

class CourseTest {

    @Nested
    class Create {

        @Test
        void withValidInputs_returnsPopulatedCourse() {
            UUID leaderId = UUID.randomUUID();
            Course course = Course.create(leaderId, "Algorithms", "A course on algorithms");

            assertThat(course.getId()).isNotNull();
            assertThat(course.getLeaderId()).isEqualTo(leaderId);
            assertThat(course.getName()).isEqualTo("Algorithms");
            assertThat(course.getDescription()).isEqualTo("A course on algorithms");
            assertThat(course.getInviteCode()).isNotNull();
            assertThat(course.isArchived()).isFalse();
            assertThat(course.getCreatedAt()).isNotNull();
            assertThat(course.getUpdatedAt()).isEqualTo(course.getCreatedAt());
        }

        @Test
        void withNullDescription_setsDescriptionToNull() {
            UUID leaderId = UUID.randomUUID();

            Course course = Course.create(leaderId, "Algorithms", null);

            assertThat(course.getDescription()).isNull();
            assertThat(course.getId()).isNotNull();
            assertThat(course.getName()).isEqualTo("Algorithms");
        }

        @ParameterizedTest
        @ValueSource(strings = {"   ", ""})
        void withBlankName_throwsDomainException(String blankName) {
            UUID leaderId = UUID.randomUUID();

            assertThatThrownBy(() -> Course.create(leaderId, blankName, null))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Course name cannot be blank.");
        }

        @Test
        void eachCall_generatesUniqueIdAndInviteCode() {
            Course a = Course.create(UUID.randomUUID(), "Course A", null);
            Course b = Course.create(UUID.randomUUID(), "Course B", null);

            assertThat(a.getId()).isNotEqualTo(b.getId());
            assertThat(a.getInviteCode()).isNotEqualTo(b.getInviteCode());
        }
    }

    @Nested
    class Reconstruct {

        @Test
        void withValidInputs_preservesAllFields() {
            UUID id = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-06-01T00:00:00Z");
            String inviteCode = UUID.randomUUID().toString();

            Course course = Course.reconstruct(id, leaderId, "Algorithms", "A description",
                    inviteCode, false, createdAt, updatedAt);

            assertThat(course.getId()).isEqualTo(id);
            assertThat(course.getLeaderId()).isEqualTo(leaderId);
            assertThat(course.getName()).isEqualTo("Algorithms");
            assertThat(course.getDescription()).isEqualTo("A description");
            assertThat(course.getInviteCode()).isEqualTo(inviteCode);
            assertThat(course.isArchived()).isFalse();
            assertThat(course.getCreatedAt()).isEqualTo(createdAt);
            assertThat(course.getUpdatedAt()).isEqualTo(updatedAt);
        }

        @Test
        void withArchivedTrue_preservesArchivedFlag() {
            UUID id = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            Instant now = Instant.now();

            Course course = Course.reconstruct(id, leaderId, "Algorithms", null,
                    inviteCode, true, now, now);

            assertThat(course.isArchived()).isTrue();
        }

        @Test
        void withNullDescription_preservesNullDescription() {
            UUID id = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            Instant now = Instant.now();

            Course course = Course.reconstruct(id, leaderId, "Algorithms", null,
                    inviteCode, false, now, now);

            assertThat(course.getDescription()).isNull();
        }

        @Test
        void withUpdatedAtEqualToCreatedAt_isAccepted() {
            UUID id = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            Instant now = Instant.parse("2024-01-01T00:00:00Z");

            Course course = Course.reconstruct(id, leaderId, "Algorithms", null,
                    inviteCode, false, now, now);

            assertThat(course.getCreatedAt()).isEqualTo(course.getUpdatedAt());
        }

        @Test
        void withUpdatedAtBeforeCreatedAt_throwsDomainException() {
            UUID id = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            Instant createdAt = Instant.parse("2024-06-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-01-01T00:00:00Z");

            assertThatThrownBy(() -> Course.reconstruct(
                    id, leaderId, "Algorithms", null,
                    inviteCode, false, createdAt, updatedAt))
                            .isInstanceOf(DomainException.class)
                            .hasMessage("Update timestamp cannot be before creation timestamp.");
        }

        static Stream<Arguments> nullArgCases() {
            Instant now = Instant.now();
            UUID id = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            return Stream.of(
                    Arguments.of("Id",          (ThrowingCallable) () -> Course.reconstruct(null, null, "Algorithms", null, inviteCode, false, now, now)),
                    Arguments.of("LeaderId",    (ThrowingCallable) () -> Course.reconstruct(id, null, "Algorithms", null, inviteCode, false, now, now)),
                    Arguments.of("Name",        (ThrowingCallable) () -> Course.reconstruct(id, leaderId, null, null, inviteCode, false, now, now)),
                    Arguments.of("InviteCode",  (ThrowingCallable) () -> Course.reconstruct(id, leaderId, "Algorithms", null, null, false, now, now)),
                    Arguments.of("CreatedAt",   (ThrowingCallable) () -> Course.reconstruct(id, leaderId, "Algorithms", null, inviteCode, false, null, now)),
                    Arguments.of("UpdatedAt",   (ThrowingCallable) () -> Course.reconstruct(id, leaderId, "Algorithms", null, inviteCode, false, now, null))
            );
        }

        @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
        @MethodSource("nullArgCases")
        void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
            assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class TransferLeadership {

        @Test
        void onActiveCourse_updatesLeaderIdAndBumpsUpdatedAt() throws InterruptedException {
            UUID leaderId = UUID.randomUUID();
            UUID newLeaderId = UUID.randomUUID();
            Course course = Course.create(leaderId, "Algorithms", null);
            Instant before = course.getUpdatedAt();
            Thread.sleep(1);

            course.transferLeadership(newLeaderId);

            assertThat(course.getLeaderId()).isEqualTo(newLeaderId);
            assertThat(course.getUpdatedAt()).isAfter(before);
        }

        @Test
        void onArchivedCourse_throwsDomainException() {
            UUID id = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID newLeaderId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            Instant now = Instant.now();
            Course course = Course.reconstruct(id, leaderId, "Algorithms", null,
                    inviteCode, true, now, now);

            assertThatThrownBy(() -> course.transferLeadership(newLeaderId))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Cannot transfer leadership of an archived course.");
        }
    }

    @Nested
    class Archive {

        @Test
        void onActiveCourse_setsArchivedTrueAndBumpsUpdatedAt() throws InterruptedException {
            Course course = Course.create(UUID.randomUUID(), "Algorithms", null);
            Instant before = course.getUpdatedAt();
            Thread.sleep(1);

            course.archive();

            assertThat(course.isArchived()).isTrue();
            assertThat(course.getUpdatedAt()).isAfter(before);
        }

        @Test
        void onAlreadyArchivedCourse_throwsDomainException() {
            UUID id = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            Instant now = Instant.now();
            Course course = Course.reconstruct(id, leaderId, "Algorithms", null,
                    inviteCode, true, now, now);

            assertThatThrownBy(course::archive)
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Course is already archived.");
        }
    }
}
