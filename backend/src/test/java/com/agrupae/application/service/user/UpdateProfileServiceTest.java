package com.agrupae.application.service.user;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.user.EmailAlreadyInUseException;
import com.agrupae.application.exception.user.UserNotFoundException;
import com.agrupae.application.port.in.user.UserProfileView;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.exception.DomainException;
import com.agrupae.domain.role.Role;
import com.agrupae.domain.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateProfileServiceTest {

    private UserRepository userRepository;
    private UpdateProfileService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new UpdateProfileService(userRepository);
    }

    @Nested
    class UpdateProfile {

        @Test
        void shouldReturnUpdatedView_whenInputIsValid() {
            UUID userId = UUID.randomUUID();
            Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-06-01T00:00:00Z");
            User user = User.reconstruct(userId, "Alice", "alice@example.com", "hash", Role.USER, createdAt, updatedAt);

            when(userRepository.findById(userId)).thenReturn(user);
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            UserProfileView view = service.handle(userId, "Alicia", "alicia@example.com");

            assertThat(view.id()).isEqualTo(userId);
            assertThat(view.name()).isEqualTo("Alicia");
            assertThat(view.email()).isEqualTo("alicia@example.com");
            assertThat(view.role()).isEqualTo(Role.USER);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getName()).isEqualTo("Alicia");
            assertThat(captor.getValue().getEmail()).isEqualTo("alicia@example.com");
        }

        @Test
        void shouldThrowUserNotFoundException_whenUserDoesNotExist() {
            UUID userId = UUID.randomUUID();
            when(userRepository.findById(userId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId, "Alicia", "alicia@example.com"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");
        }

        @Test
        void shouldPropagateDomainException_whenProfileDataIsInvalid() {
            UUID userId = UUID.randomUUID();
            Instant now = Instant.now();
            User user = User.reconstruct(userId, "Alice", "alice@example.com", "hash", Role.USER, now, now);

            when(userRepository.findById(userId)).thenReturn(user);

            // Blank name triggers DomainException inside user.updateProfile()
            assertThatThrownBy(() -> service.handle(userId, "   ", "alicia@example.com"))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("User name cannot be blank.");
        }

        @Test
        void shouldNotCheckEmailUniqueness_whenEmailIsUnchanged() {
            UUID userId = UUID.randomUUID();
            Instant now = Instant.now();
            User user = User.reconstruct(userId, "Alice", "alice@example.com", "hash", Role.USER, now, now);

            when(userRepository.findById(userId)).thenReturn(user);
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            service.handle(userId, "Alice Updated", "alice@example.com");

            verify(userRepository, never()).findByEmail(anyString());
        }

        @Test
        void shouldThrowEmailAlreadyInUseException_whenNewEmailBelongsToAnotherUser() {
            UUID userId = UUID.randomUUID();
            Instant now = Instant.now();
            User user = User.reconstruct(userId, "Alice", "alice@example.com", "hash", Role.USER, now, now);
            User otherUser = User.reconstruct(UUID.randomUUID(), "Bob", "bob@example.com", "hash", Role.USER, now, now);

            when(userRepository.findById(userId)).thenReturn(user);
            when(userRepository.findByEmail("bob@example.com")).thenReturn(otherUser);

            assertThatThrownBy(() -> service.handle(userId, "Alice", "bob@example.com"))
                    .isInstanceOf(EmailAlreadyInUseException.class)
                    .hasMessage("Email is already in use.");
        }
    }
}
