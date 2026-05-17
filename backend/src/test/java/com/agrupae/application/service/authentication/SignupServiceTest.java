package com.agrupae.application.service.authentication;

import java.time.Duration;

import com.agrupae.application.exception.user.UserAlreadyExistsException;
import com.agrupae.application.port.out.authentication.PasswordEncoder;
import com.agrupae.application.port.out.authentication.RefreshTokenRepository;
import com.agrupae.application.port.out.authentication.TokenConfig;
import com.agrupae.application.port.out.authentication.TokenHasher;
import com.agrupae.application.port.out.authentication.TokenPair;
import com.agrupae.application.port.out.authentication.TokenProvider;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.exception.DomainException;
import com.agrupae.domain.refresh_token.RefreshToken;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SignupServiceTest {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private PasswordEncoder passwordEncoder;
    private TokenProvider tokenProvider;
    private TokenHasher tokenHasher;
    private TokenConfig tokenConfig;
    private SignupService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        tokenProvider = mock(TokenProvider.class);
        tokenHasher = mock(TokenHasher.class);
        tokenConfig = mock(TokenConfig.class);
        service = new SignupService(tokenProvider, tokenHasher, userRepository, passwordEncoder, refreshTokenRepository,
                tokenConfig);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(tokenProvider.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(tokenHasher.hash(anyString())).thenReturn("hashedToken");
        when(tokenConfig.refreshTokenTTL()).thenReturn(Duration.ofDays(7));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Nested
    class Signup {

        @Test
        void shouldReturnTokenPair_whenCredentialsAreValid() {
            when(userRepository.findByEmail("alice@example.com")).thenReturn(null);

            TokenPair result = service.handle("Alice", "alice@example.com", "rawPassword");

            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isNotNull();
            assertThat(result.rawRefreshToken()).isNotNull();
        }

        @Test
        void shouldThrowUserAlreadyExistsException_whenEmailIsAlreadyTaken() {
            User existing = User.create("Alice", "alice@example.com", "hash", Role.USER);
            when(userRepository.findByEmail("alice@example.com")).thenReturn(existing);

            assertThatThrownBy(() -> service.handle("Alice", "alice@example.com", "rawPassword"))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("User already exists.");
        }

        @Test
        void shouldHashPassword_beforeSavingUser() {
            when(userRepository.findByEmail("alice@example.com")).thenReturn(null);

            service.handle("Alice", "alice@example.com", "rawPassword");

            verify(passwordEncoder).encode("rawPassword");

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getPasswordHash()).isEqualTo("encodedPassword");
        }

        @Test
        void shouldSaveNewUser_withRoleUser() {
            when(userRepository.findByEmail("alice@example.com")).thenReturn(null);

            service.handle("Alice", "alice@example.com", "rawPassword");

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getRole()).isEqualTo(Role.USER);
        }

        @Test
        void shouldSaveRefreshToken_withHashedToken() {
            when(userRepository.findByEmail("alice@example.com")).thenReturn(null);
            when(tokenHasher.hash(anyString())).thenReturn("hashedRefreshToken");

            service.handle("Alice", "alice@example.com", "rawPassword");

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenRepository).save(captor.capture());
            assertThat(captor.getValue().getTokenHash()).isEqualTo("hashedRefreshToken");
        }

        @Test
        void shouldReturnRawRefreshToken_notHashedToken() {
            when(userRepository.findByEmail("alice@example.com")).thenReturn(null);
            when(tokenHasher.hash(anyString())).thenReturn("hashedRefreshToken");

            TokenPair result = service.handle("Alice", "alice@example.com", "rawPassword");

            assertThat(result.rawRefreshToken()).isNotEqualTo("hashedRefreshToken");
        }

        @Test
        void shouldPropagateDomainException_whenUserDataIsInvalid() {
            when(userRepository.findByEmail("alice@example.com")).thenReturn(null);
            when(passwordEncoder.encode(anyString())).thenReturn("   ");

            assertThatThrownBy(() -> service.handle("Alice", "alice@example.com", "rawPassword"))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Password hash cannot be blank.");
        }
    }
}
