package com.agrupae.application.service.authentication;

import java.util.UUID;

import com.agrupae.application.port.in.authentication.RefreshUseCase;
import com.agrupae.application.port.out.authentication.RefreshTokenRepository;
import com.agrupae.application.port.out.authentication.TokenConfig;
import com.agrupae.application.port.out.authentication.TokenHasher;
import com.agrupae.application.port.out.authentication.TokenProvider;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.exception.InvalidTokenException;
import com.agrupae.domain.exception.TokenExpiredException;
import com.agrupae.domain.exception.TokenRevokedException;
import com.agrupae.domain.exception.UserNotFoundException;
import com.agrupae.domain.refresh_token.RefreshToken;
import com.agrupae.domain.refresh_token.TokenPair;
import com.agrupae.domain.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshService implements RefreshUseCase {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final TokenHasher tokenHasher;
    private final TokenConfig tokenConfig;

    public TokenPair handle(UUID userId, String rawRefreshToken) {
        User user = this.userRepository.findById(userId);

        if (user == null) throw new UserNotFoundException();

        String refreshTokenHash = this.tokenHasher.hash(rawRefreshToken);
        RefreshToken refreshToken = this.refreshTokenRepository.findByTokenHash(refreshTokenHash);

        if (refreshToken == null) throw new InvalidTokenException();

        if (!refreshToken.getUserId().equals(user.getId())) {
            this.refreshTokenRepository.revokeAllByFamilyId(refreshToken.getTokenFamilyId());
            throw new InvalidTokenException();
        }

        if (refreshToken.isRevoked()) {
            this.refreshTokenRepository.revokeAllByFamilyId(refreshToken.getTokenFamilyId());
            throw new TokenRevokedException();
        }

        if (refreshToken.isExpired()) {
            throw new TokenExpiredException();
        }

        refreshToken.revoke();
        this.refreshTokenRepository.save(refreshToken);

        String newAccessToken = this.tokenProvider.generateAccessToken(user);
        String newRawRefreshToken = UUID.randomUUID().toString();
        RefreshToken newRefreshToken = RefreshToken.createForRotation(
            userId,
            this.tokenHasher.hash(newRawRefreshToken),
            refreshToken.getTokenFamilyId(),
            this.tokenConfig.refreshTokenTTL()
        );

        this.refreshTokenRepository.save(newRefreshToken);

        return new TokenPair(newAccessToken, newRawRefreshToken);
    }
}
