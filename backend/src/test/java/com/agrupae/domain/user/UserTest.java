package com.agrupae.domain.user;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import com.agrupae.domain.exception.DomainException;
import com.agrupae.domain.role.Role;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Nested
    class Create {

        @Test
        void withValidInputs_returnsUserWithExpectedFields() {
            User user = User.create("Alice", "alice@example.com", "hash", Role.USER);

            assertThat(user.getId()).isNotNull();
            assertThat(user.getName()).isEqualTo("Alice");
            assertThat(user.getEmail()).isEqualTo("alice@example.com");
            assertThat(user.getPasswordHash()).isEqualTo("hash");
            assertThat(user.getRole()).isEqualTo(Role.USER);
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt());
        }

        @Test
        void withBlankName_throwsDomainException() {
            assertThatThrownBy(() -> User.create("  ", "alice@example.com", "hash", Role.USER))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("User name cannot be blank.");
        }

        @Test
        void withBlankEmail_throwsDomainException() {
            assertThatThrownBy(() -> User.create("Alice", "  ", "hash", Role.USER))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Email cannot be blank.");
        }

        @Test
        void withInvalidEmailFormat_throwsDomainException() {
            assertThatThrownBy(() -> User.create("Alice", "notanemail", "hash", Role.USER))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Invalid email format.");
        }

        @Test
        void withBlankPasswordHash_throwsDomainException() {
            assertThatThrownBy(() -> User.create("Alice", "alice@example.com", "  ", Role.USER))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Password hash cannot be blank.");
        }

        static Stream<Arguments> nullArgCases() {
            return Stream.of(
                    Arguments.of("Name",         (ThrowingCallable) () -> User.create(null, "alice@example.com", "hash", Role.USER)),
                    Arguments.of("Email",        (ThrowingCallable) () -> User.create("Alice", null, "hash", Role.USER)),
                    Arguments.of("PasswordHash", (ThrowingCallable) () -> User.create("Alice", "alice@example.com", null, Role.USER)),
                    Arguments.of("Role",         (ThrowingCallable) () -> User.create("Alice", "alice@example.com", "hash", null))
            );
        }

        @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
        @MethodSource("nullArgCases")
        void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
            assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class Reconstruct {

        @Test
        void withValidInputs_preservesAllFields() {
            UUID id = UUID.randomUUID();
            Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-06-01T00:00:00Z");
            User user = User.reconstruct(id, "Bob", "bob@example.com", "hash", Role.ADMIN, createdAt, updatedAt);

            assertThat(user.getId()).isEqualTo(id);
            assertThat(user.getName()).isEqualTo("Bob");
            assertThat(user.getEmail()).isEqualTo("bob@example.com");
            assertThat(user.getPasswordHash()).isEqualTo("hash");
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
            assertThat(user.getCreatedAt()).isEqualTo(createdAt);
            assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
        }

        @Test
        void withUpdatedAtBeforeCreatedAt_throwsDomainException() {
            Instant createdAt = Instant.parse("2024-06-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-01-01T00:00:00Z");

            assertThatThrownBy(() -> User.reconstruct(
                    UUID.randomUUID(), "Bob", "bob@example.com", "hash", Role.USER, createdAt, updatedAt))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Update timestamp cannot be before creation timestamp.");
        }

        static Stream<Arguments> nullArgCases() {
            Instant now = Instant.now();
            UUID id = UUID.randomUUID();
            return Stream.of(
                    Arguments.of("Id",           (ThrowingCallable) () -> User.reconstruct(null, "Bob", "bob@example.com", "hash", Role.USER, now, now)),
                    Arguments.of("Name",         (ThrowingCallable) () -> User.reconstruct(id, null, "bob@example.com", "hash", Role.USER, now, now)),
                    Arguments.of("Email",        (ThrowingCallable) () -> User.reconstruct(id, "Bob", null, "hash", Role.USER, now, now)),
                    Arguments.of("PasswordHash", (ThrowingCallable) () -> User.reconstruct(id, "Bob", "bob@example.com", null, Role.USER, now, now)),
                    Arguments.of("Role",         (ThrowingCallable) () -> User.reconstruct(id, "Bob", "bob@example.com", "hash", null, now, now)),
                    Arguments.of("CreatedAt",    (ThrowingCallable) () -> User.reconstruct(id, "Bob", "bob@example.com", "hash", Role.USER, null, now)),
                    Arguments.of("UpdatedAt",    (ThrowingCallable) () -> User.reconstruct(id, "Bob", "bob@example.com", "hash", Role.USER, now, null))
            );
        }

        @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
        @MethodSource("nullArgCases")
        void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
            assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class UpdateProfile {

        @Test
        void withValidInputs_updatesNameEmailAndTimestamp() {
            User user = User.create("Alice", "alice@example.com", "hash", Role.USER);
            Instant before = user.getUpdatedAt();

            user.updateProfile("Alicia", "alicia@example.com");

            assertThat(user.getName()).isEqualTo("Alicia");
            assertThat(user.getEmail()).isEqualTo("alicia@example.com");
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(before);
        }

        @Test
        void withSameValues_stillBumpsUpdatedAt() {
            User user = User.create("Alice", "alice@example.com", "hash", Role.USER);
            Instant before = user.getUpdatedAt();

            user.updateProfile("Alice", "alice@example.com");

            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(before);
        }

        @Test
        void withInvalidEmailFormat_throwsDomainException() {
            User user = User.create("Alice", "alice@example.com", "hash", Role.USER);

            assertThatThrownBy(() -> user.updateProfile("Alice", "notanemail"))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Invalid email format.");
        }

        static Stream<Arguments> nullArgCases() {
            User user = User.create("Alice", "alice@example.com", "hash", Role.USER);
            return Stream.of(
                    Arguments.of("Name",  (ThrowingCallable) () -> user.updateProfile(null, "alice@example.com")),
                    Arguments.of("Email", (ThrowingCallable) () -> user.updateProfile("Alice", null))
            );
        }

        @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
        @MethodSource("nullArgCases")
        void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
            assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
        }
    }
}
