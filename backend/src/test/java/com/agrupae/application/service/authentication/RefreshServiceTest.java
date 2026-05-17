package com.agrupae.application.service.authentication;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.auth.InvalidTokenException;
import com.agrupae.application.exception.auth.TokenExpiredException;
import com.agrupae.application.exception.auth.TokenRevokedException;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshServiceTest {

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;
    private TokenProvider tokenProvider;
    private TokenHasher tokenHasher;
    private TokenConfig tokenConfig;
    private RefreshService service;

    private User validUser;
    private UUID familyId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        userRepository = mock(UserRepository.class);
        tokenProvider = mock(TokenProvider.class);
        tokenHasher = mock(TokenHasher.class);
        tokenConfig = mock(TokenConfig.class);
        service = new RefreshService(userRepository, refreshTokenRepository, tokenProvider, tokenHasher, tokenConfig);

        userId = UUID.randomUUID();
        familyId = UUID.randomUUID();
        validUser = User.create("Alice", "alice@example.com", "hash", Role.USER);

        when(tokenHasher.hash(anyString())).thenReturn("hashedToken");
        when(tokenProvider.generateAccessToken(any(User.class))).thenReturn("newAccessToken");
        when(tokenConfig.refreshTokenTTL()).thenReturn(Duration.ofDays(7));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private RefreshToken validToken() {
        Instant now = Instant.now();
        return RefreshToken.reconstruct(
                UUID.randomUUID(), userId, familyId, "hashedToken",
                false, now.plus(Duration.ofDays(7)), now, now);
    }

    private RefreshToken revokedToken() {
        Instant now = Instant.now();
        return RefreshToken.reconstruct(
                UUID.randomUUID(), userId, familyId, "hashedToken",
                true, now.plus(Duration.ofDays(7)), now, now);
    }

    private RefreshToken expiredToken() {
        Instant now = Instant.now();
        return RefreshToken.reconstruct(
                UUID.randomUUID(), userId, familyId, "hashedToken",
                false, now.minus(Duration.ofSeconds(1)), now, now);
    }

    @Nested
    class Refresh {

        @Test
        void shouldReturnNewTokenPair_whenTokenIsValid() {
            when(refreshTokenRepository.findByTokenHash("hashedToken")).thenReturn(validToken());
            when(userRepository.findById(userId)).thenReturn(validUser);

            TokenPair result = service.handle("rawToken");

            assertThat(result.accessToken()).isEqualTo("newAccessToken");
            assertThat(result.rawRefreshToken()).isNotNull();
        }

        @Test
        void shouldThrowInvalidTokenException_whenTokenHashNotFound() {
            when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(null);

            assertThatThrownBy(() -> service.handle("rawToken"))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("Invalid refresh token.");
        }

        @Test
        void shouldThrowTokenRevokedException_andRevokeFamily_whenTokenIsRevoked() {
            when(refreshTokenRepository.findByTokenHash("hashedToken")).thenReturn(revokedToken());

            assertThatThrownBy(() -> service.handle("rawToken"))
                    .isInstanceOf(TokenRevokedException.class)
                    .hasMessage("Refresh token has been revoked.");

            verify(refreshTokenRepository).revokeAllByFamilyId(familyId);
        }

        @Test
        void shouldThrowTokenExpiredException_whenTokenIsExpired() {
            when(refreshTokenRepository.findByTokenHash("hashedToken")).thenReturn(expiredToken());

            assertThatThrownBy(() -> service.handle("rawToken"))
                    .isInstanceOf(TokenExpiredException.class)
                    .hasMessage("Refresh token has expired.");
        }

        @Test
        void shouldThrowInvalidTokenException_whenUserNotFound() {
            when(refreshTokenRepository.findByTokenHash("hashedToken")).thenReturn(validToken());
            when(userRepository.findById(userId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle("rawToken"))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("Invalid refresh token.");
        }

        @Test
        void shouldRotateTokenFamily_newTokenHasSameFamilyId() {
            when(refreshTokenRepository.findByTokenHash("hashedToken")).thenReturn(validToken());
            when(userRepository.findById(userId)).thenReturn(validUser);

            service.handle("rawToken");

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenRepository, times(2)).save(captor.capture());
            RefreshToken newToken = captor.getAllValues().get(1);
            assertThat(newToken.getTokenFamilyId()).isEqualTo(familyId);
        }

        @Test
        void shouldRevokeOldToken_beforeIssuingNew() {
            when(refreshTokenRepository.findByTokenHash("hashedToken")).thenReturn(validToken());
            when(userRepository.findById(userId)).thenReturn(validUser);

            service.handle("rawToken");

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenRepository, times(2)).save(captor.capture());
            assertThat(captor.getAllValues().get(0).isRevoked()).isTrue();
        }
    }
}
