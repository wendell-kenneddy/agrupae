package com.agrupae.application.service.authentication;

import java.time.Duration;

import com.agrupae.application.exception.auth.InvalidCredentialsException;
import com.agrupae.application.port.out.authentication.PasswordEncoder;
import com.agrupae.application.port.out.authentication.RefreshTokenRepository;
import com.agrupae.application.port.out.authentication.TokenConfig;
import com.agrupae.application.port.out.authentication.TokenHasher;
import com.agrupae.application.port.out.authentication.TokenPair;
import com.agrupae.application.port.out.authentication.TokenProvider;
import com.agrupae.application.port.out.user.UserRepository;
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

class LoginServiceTest {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private PasswordEncoder passwordEncoder;
    private TokenProvider tokenProvider;
    private TokenHasher tokenHasher;
    private TokenConfig tokenConfig;
    private LoginService service;

    private User validUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        tokenProvider = mock(TokenProvider.class);
        tokenHasher = mock(TokenHasher.class);
        tokenConfig = mock(TokenConfig.class);
        service = new LoginService(userRepository, refreshTokenRepository, passwordEncoder, tokenProvider, tokenHasher, tokenConfig);

        validUser = User.create("Alice", "alice@example.com", "storedHash", Role.USER);

        when(tokenProvider.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(tokenHasher.hash(anyString())).thenReturn("hashedToken");
        when(tokenConfig.refreshTokenTTL()).thenReturn(Duration.ofDays(7));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Nested
    class Login {

        @Test
        void shouldReturnTokenPair_whenCredentialsAreValid() {
            when(userRepository.findByEmail("alice@example.com")).thenReturn(validUser);
            when(passwordEncoder.matches("rawPassword", "storedHash")).thenReturn(true);

            TokenPair result = service.handle("alice@example.com", "rawPassword");

            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isNotNull();
            assertThat(result.rawRefreshToken()).isNotNull();

            verify(passwordEncoder).matches("rawPassword", "storedHash");
        }

        @Test
        void shouldThrowInvalidCredentialsException_whenEmailNotFound() {
            when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);

            assertThatThrownBy(() -> service.handle("unknown@example.com", "rawPassword"))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Invalid email or password.");
        }

        @Test
        void shouldThrowInvalidCredentialsException_whenPasswordDoesNotMatch() {
            when(userRepository.findByEmail("alice@example.com")).thenReturn(validUser);
            when(passwordEncoder.matches("wrongPassword", "storedHash")).thenReturn(false);

            assertThatThrownBy(() -> service.handle("alice@example.com", "wrongPassword"))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Invalid email or password.");
        }

        @Test
        void shouldSaveRefreshToken_onSuccess() {
            when(userRepository.findByEmail("alice@example.com")).thenReturn(validUser);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

            service.handle("alice@example.com", "rawPassword");

            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }

        @Test
        void shouldHashRefreshToken_beforeSaving() {
            when(userRepository.findByEmail("alice@example.com")).thenReturn(validUser);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
;
            service.handle("alice@example.com", "rawPassword");

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenRepository).save(captor.capture());
            assertThat(captor.getValue().getTokenHash()).isEqualTo("hashedToken");
        }
    }
}
