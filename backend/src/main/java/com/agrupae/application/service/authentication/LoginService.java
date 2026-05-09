package com.agrupae.application.service.authentication;

import java.util.UUID;

import com.agrupae.application.exception.auth.InvalidCredentialsException;
import com.agrupae.application.port.in.authentication.LoginUseCase;
import com.agrupae.application.port.out.authentication.PasswordEncoder;
import com.agrupae.application.port.out.authentication.RefreshTokenRepository;
import com.agrupae.application.port.out.authentication.TokenConfig;
import com.agrupae.application.port.out.authentication.TokenHasher;
import com.agrupae.application.port.out.authentication.TokenPair;
import com.agrupae.application.port.out.authentication.TokenProvider;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.refresh_token.RefreshToken;
import com.agrupae.domain.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginService implements LoginUseCase {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TokenHasher tokenHasher;
    private final TokenConfig tokenConfig;

    public TokenPair handle(String email, String password) {
        User user = this.userRepository.findByEmail(email);

        if (user == null || !this.passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = this.tokenProvider.generateAccessToken(user);
        String rawRefreshToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.create(
                user.getId(),
                this.tokenHasher.hash(rawRefreshToken),
                this.tokenConfig.refreshTokenTTL());

        this.refreshTokenRepository.save(refreshToken);

        return new TokenPair(accessToken, rawRefreshToken);
    }
}
