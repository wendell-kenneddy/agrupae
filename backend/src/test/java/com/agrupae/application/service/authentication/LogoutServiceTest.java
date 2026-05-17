package com.agrupae.application.service.authentication;

import com.agrupae.application.exception.auth.InvalidTokenException;
import com.agrupae.application.port.out.authentication.RefreshTokenRepository;
import com.agrupae.application.port.out.authentication.TokenHasher;
import com.agrupae.domain.refresh_token.RefreshToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogoutServiceTest {

    private RefreshTokenRepository refreshTokenRepository;
    private TokenHasher tokenHasher;
    private LogoutService service;

    private UUID familyId;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        tokenHasher = mock(TokenHasher.class);
        service = new LogoutService(refreshTokenRepository, tokenHasher);

        familyId = UUID.randomUUID();
        when(tokenHasher.hash(anyString())).thenReturn("hashedToken");
    }

    private RefreshToken validToken() {
        Instant now = Instant.now();
        return RefreshToken.reconstruct(
                UUID.randomUUID(), UUID.randomUUID(), familyId, "hashedToken",
                false, now.plus(Duration.ofDays(7)), now, now);
    }

    @Nested
    class Logout {

        @Test
        void shouldRevokeEntireTokenFamily_whenTokenIsValid() {
            when(refreshTokenRepository.findByTokenHash("hashedToken")).thenReturn(validToken());

            service.handle("rawToken");

            verify(refreshTokenRepository).revokeAllByFamilyId(familyId);
        }

        @Test
        void shouldThrowInvalidTokenException_whenTokenNotFound() {
            when(refreshTokenRepository.findByTokenHash("hashedToken")).thenReturn(null);

            assertThatThrownBy(() -> service.handle("rawToken"))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("Invalid refresh token.");
        }

        @Test
        void shouldHashToken_beforeLookup() {
            when(refreshTokenRepository.findByTokenHash("hashedToken")).thenReturn(validToken());

            service.handle("rawToken");

            ArgumentCaptor<String> hasherCaptor = ArgumentCaptor.forClass(String.class);
            verify(tokenHasher).hash(hasherCaptor.capture());
            assertThat(hasherCaptor.getValue()).isEqualTo("rawToken");

            verify(refreshTokenRepository).findByTokenHash("hashedToken");
        }
    }
}
