package com.agrupae.application.service.user;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.user.UserNotFoundException;
import com.agrupae.application.port.in.user.UserProfileView;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.role.Role;
import com.agrupae.domain.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetUserProfileServiceTest {

    private UserRepository userRepository;
    private GetUserProfileService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new GetUserProfileService(userRepository);
    }

    @Nested
    class GetProfile {

        @Test
        void shouldReturnUserProfileView_whenUserExists() {
            UUID userId = UUID.randomUUID();
            Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-06-01T00:00:00Z");
            User user = User.reconstruct(userId, "Alice", "alice@example.com", "hash", Role.USER, createdAt, updatedAt);

            when(userRepository.findById(userId)).thenReturn(user);

            UserProfileView view = service.handle(userId);

            assertThat(view.id()).isEqualTo(userId);
            assertThat(view.name()).isEqualTo("Alice");
            assertThat(view.email()).isEqualTo("alice@example.com");
            assertThat(view.role()).isEqualTo(Role.USER);
            assertThat(view.createdAt()).isEqualTo(createdAt);
            assertThat(view.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        void shouldThrowUserNotFoundException_whenUserDoesNotExist() {
            UUID userId = UUID.randomUUID();
            when(userRepository.findById(userId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");
        }
    }
}
