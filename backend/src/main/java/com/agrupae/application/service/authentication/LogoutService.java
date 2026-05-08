package com.agrupae.application.service.authentication;

import com.agrupae.application.exception.auth.InvalidTokenException;
import com.agrupae.application.port.in.authentication.LogoutUseCase;
import com.agrupae.application.port.out.authentication.RefreshTokenRepository;
import com.agrupae.application.port.out.authentication.TokenHasher;
import com.agrupae.domain.refresh_token.RefreshToken;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasher tokenHasher;

    public void handle(String rawRefreshToken) {
        String refreshTokenHash = this.tokenHasher.hash(rawRefreshToken);
        RefreshToken refreshToken = this.refreshTokenRepository.findByTokenHash(refreshTokenHash);

        if (refreshToken == null) throw new InvalidTokenException();

        this.refreshTokenRepository.revokeAllByFamilyId(refreshToken.getTokenFamilyId());
    }
}
