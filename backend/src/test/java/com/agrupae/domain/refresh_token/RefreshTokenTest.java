package com.agrupae.domain.refresh_token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.agrupae.domain.exception.DomainException;

public class RefreshTokenTest {
    private UUID userId;
    private Duration duration;
    private String tokenHash;
    private Instant now;

    @BeforeEach
    void setup() {
        this.userId = UUID.randomUUID();
        this.duration = Duration.ofDays(7);
        this.tokenHash = "tokenHash";
        this.now = Instant.now();
    }

    @Nested
    class Create {

        @Test
        void withValidInputs_shouldReturnValidRefreshToken() {
            RefreshToken refreshToken = RefreshToken.create(userId, tokenHash, duration);

            assertThat(refreshToken.getId()).isNotNull();
            assertThat(refreshToken.getUserId()).isEqualTo(userId);
            assertThat(refreshToken.getTokenHash()).isEqualTo(tokenHash);
            assertThat(refreshToken.getTokenFamilyId()).isNotNull();
            assertThat(refreshToken.isRevoked()).isEqualTo(false);
            assertThat(refreshToken.isExpired()).isEqualTo(false);
            assertThat(refreshToken.getExpiresAt()).isAfterOrEqualTo(now.plus(duration));
            assertThat(refreshToken.getCreatedAt()).isNotNull();
            assertThat(refreshToken.getUpdatedAt()).isEqualTo(refreshToken.getCreatedAt());
        }

        @Test
        void withBlankTokenHash_throwsDomainException() {
            assertThatThrownBy(() -> RefreshToken.create(userId, "  ", duration))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Token hash cannot be blank.");
        }

        @Test
        void withEmptyTokenHash_throwsDomainException() {
            assertThatThrownBy(() -> RefreshToken.create(userId, "", duration))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Token hash cannot be blank.");
        }

        @Test
        void withExpiresAtBeforeCreatedAt_throwsDomainException() {
             assertThatThrownBy(() -> RefreshToken.create(userId, tokenHash, Duration.ofSeconds(-1)))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Refresh token must expire after creation timestamp.");
        }

        @Test
        void withZeroDuration_throwsDomainException() {
            assertThatThrownBy(() -> RefreshToken.create(userId, tokenHash, Duration.ZERO))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Refresh token must expire after creation timestamp.");
        }

        static Stream<Arguments> nullArgCases() {
            return Stream.of(
                Arguments.of("userId", (ThrowingCallable) () -> RefreshToken.create(null, "tokenHash", Duration.ofDays(7))),
                Arguments.of("tokenHash", (ThrowingCallable) () -> RefreshToken.create(UUID.randomUUID(), null, Duration.ofDays(7))),
                Arguments.of("duration", (ThrowingCallable) () -> RefreshToken.create(UUID.randomUUID(), "tokenHash", null))
            );
        }

        @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
        @MethodSource("nullArgCases")
        void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
            assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class CreateForRotation {

        @Test
        void withValidInputs_preservesProvidedFamilyId() {
            UUID familyId = UUID.randomUUID();

            RefreshToken refreshToken = RefreshToken.createForRotation(userId, tokenHash, familyId, duration);

            assertThat(refreshToken.getTokenFamilyId()).isEqualTo(familyId);
            assertThat(refreshToken.getId()).isNotNull();
            assertThat(refreshToken.getId()).isNotEqualTo(familyId);
            assertThat(refreshToken.getUserId()).isEqualTo(userId);
            assertThat(refreshToken.getTokenHash()).isEqualTo(tokenHash);
            assertThat(refreshToken.isRevoked()).isFalse();
            assertThat(refreshToken.isExpired()).isFalse();
        }

        @Test
        void withSameFamilyId_producesDifferentTokenId() {
            UUID familyId = UUID.randomUUID();

            RefreshToken first = RefreshToken.createForRotation(userId, tokenHash, familyId, duration);
            RefreshToken second = RefreshToken.createForRotation(userId, tokenHash, familyId, duration);

            assertThat(first.getId()).isNotEqualTo(second.getId());
            assertThat(first.getTokenFamilyId()).isEqualTo(second.getTokenFamilyId()).isEqualTo(familyId);
        }

        @Test
        void withBlankTokenHash_throwsDomainException() {
            UUID familyId = UUID.randomUUID();

            assertThatThrownBy(() -> RefreshToken.createForRotation(userId, "  ", familyId, duration))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Token hash cannot be blank.");
        }

        @Test
        void withNegativeDuration_throwsDomainException() {
            UUID familyId = UUID.randomUUID();

            assertThatThrownBy(() -> RefreshToken.createForRotation(userId, tokenHash, familyId, Duration.ofSeconds(-1)))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Refresh token must expire after creation timestamp.");
        }

        static Stream<Arguments> nullArgCases() {
            return Stream.of(
                Arguments.of("userId", (ThrowingCallable) () -> RefreshToken.createForRotation(null, "tokenHash", UUID.randomUUID(), Duration.ofDays(7))),
                Arguments.of("tokenHash", (ThrowingCallable) () -> RefreshToken.createForRotation(UUID.randomUUID(), null, UUID.randomUUID(), Duration.ofDays(7))),
                Arguments.of("familyId", (ThrowingCallable) () -> RefreshToken.createForRotation(UUID.randomUUID(), "tokenHash", null, Duration.ofDays(7))),
                Arguments.of("duration", (ThrowingCallable) () -> RefreshToken.createForRotation(UUID.randomUUID(), "tokenHash", UUID.randomUUID(), null))
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
        void withValidInputs_returnsTokenWithExactFieldValues() {
            UUID id = UUID.randomUUID();
            UUID familyId = UUID.randomUUID();
            Instant createdAt = now.minusSeconds(3600);
            Instant updatedAt = now.minusSeconds(1800);
            Instant expiresAt = now.minusSeconds(60);

            RefreshToken refreshToken = RefreshToken.reconstruct(
                    id, userId, familyId, tokenHash, true, expiresAt, createdAt, updatedAt);

            assertThat(refreshToken.getId()).isEqualTo(id);
            assertThat(refreshToken.getUserId()).isEqualTo(userId);
            assertThat(refreshToken.getTokenFamilyId()).isEqualTo(familyId);
            assertThat(refreshToken.getTokenHash()).isEqualTo(tokenHash);
            assertThat(refreshToken.isRevoked()).isTrue();
            assertThat(refreshToken.getExpiresAt()).isEqualTo(expiresAt);
            assertThat(refreshToken.getCreatedAt()).isEqualTo(createdAt);
            assertThat(refreshToken.getUpdatedAt()).isEqualTo(updatedAt);
        }

        @Test
        void withExpiredExpiresAt_isExpiredReturnsTrue() {
            RefreshToken refreshToken = RefreshToken.reconstruct(
                    UUID.randomUUID(), userId, UUID.randomUUID(), tokenHash, false,
                    now.minusSeconds(1), now.minusSeconds(3600), now.minusSeconds(3600));

            assertThat(refreshToken.isExpired()).isTrue();
        }

        @Test
        void withRevokedTrue_preservesRevokedState() {
            RefreshToken refreshToken = RefreshToken.reconstruct(
                    UUID.randomUUID(), userId, UUID.randomUUID(), tokenHash, true,
                    now.plus(duration), now, now);

            assertThat(refreshToken.isRevoked()).isTrue();
        }
    }

    @Nested
    class Revoke {

        @Test
        void onActiveToken_setsRevokedTrueAndBumpsUpdatedAt() throws InterruptedException {
            RefreshToken refreshToken = RefreshToken.create(userId, tokenHash, duration);
            Instant before = refreshToken.getUpdatedAt();
            Thread.sleep(10);

            refreshToken.revoke();

            assertThat(refreshToken.isRevoked()).isTrue();
            assertThat(refreshToken.getUpdatedAt()).isAfter(before);
        }

        @Test
        void onAlreadyRevokedToken_isIdempotent() throws InterruptedException {
            RefreshToken refreshToken = RefreshToken.create(userId, tokenHash, duration);
            refreshToken.revoke();
            Instant updatedAtAfterFirstRevoke = refreshToken.getUpdatedAt();
            Thread.sleep(10);

            refreshToken.revoke();

            assertThat(refreshToken.isRevoked()).isTrue();
            assertThat(refreshToken.getUpdatedAt()).isEqualTo(updatedAtAfterFirstRevoke);
        }

        @Test
        void reconstructedRevokedToken_revoke_isNoOp() throws InterruptedException {
            Instant createdAt = now.minusSeconds(3600);
            Instant updatedAt = now.minusSeconds(1800);
            RefreshToken refreshToken = RefreshToken.reconstruct(
                    UUID.randomUUID(), userId, UUID.randomUUID(), tokenHash, true,
                    now.plus(duration), createdAt, updatedAt);
            Thread.sleep(10);

            refreshToken.revoke();

            assertThat(refreshToken.isRevoked()).isTrue();
            assertThat(refreshToken.getUpdatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    class IsExpired {

        @Test
        void whenExpiresAtInFuture_returnsFalse() {
            RefreshToken refreshToken = RefreshToken.reconstruct(
                    UUID.randomUUID(), userId, UUID.randomUUID(), tokenHash, false,
                    now.plusSeconds(3600), now, now);

            assertThat(refreshToken.isExpired()).isFalse();
        }

        @Test
        void whenExpiresAtInPast_returnsTrue() {
            RefreshToken refreshToken = RefreshToken.reconstruct(
                    UUID.randomUUID(), userId, UUID.randomUUID(), tokenHash, false,
                    now.minusSeconds(1), now.minusSeconds(3600), now.minusSeconds(3600));

            assertThat(refreshToken.isExpired()).isTrue();
        }

    }
}
