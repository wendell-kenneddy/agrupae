package com.agrupae.application.service.authentication;

import java.util.UUID;

import com.agrupae.application.port.in.authentication.LogoutUseCase;
import com.agrupae.application.port.out.authentication.RefreshTokenRepository;
import com.agrupae.application.port.out.authentication.TokenHasher;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.exception.InvalidTokenException;
import com.agrupae.domain.exception.UserNotFoundException;
import com.agrupae.domain.refresh_token.RefreshToken;
import com.agrupae.domain.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasher tokenHasher;

    public void handle(UUID userId, String rawRefreshToken) {
        User user = this.userRepository.findById(userId);

        if (user == null) throw new UserNotFoundException();

        String refreshTokenHash = this.tokenHasher.hash(rawRefreshToken);
        RefreshToken refreshToken = this.refreshTokenRepository.findByTokenHash(refreshTokenHash);

        if (refreshToken == null) throw new InvalidTokenException();

        this.refreshTokenRepository.revokeAllByFamilyId(refreshToken.getTokenFamilyId());
    }
}
