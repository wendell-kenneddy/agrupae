package com.agrupae.application.service.authentication;

import java.util.UUID;

import com.agrupae.application.port.in.authentication.SignupUseCase;
import com.agrupae.application.port.out.authentication.PasswordEncoder;
import com.agrupae.application.port.out.authentication.RefreshTokenRepository;
import com.agrupae.application.port.out.authentication.TokenConfig;
import com.agrupae.application.port.out.authentication.TokenHasher;
import com.agrupae.application.port.out.authentication.TokenProvider;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.exception.UserAlreadyExistsException;
import com.agrupae.domain.refresh_token.RefreshToken;
import com.agrupae.domain.refresh_token.TokenPair;
import com.agrupae.domain.role.Role;
import com.agrupae.domain.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SignupService implements SignupUseCase {
    private final TokenProvider tokenProvider;
    private final TokenHasher tokenHasher;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenConfig tokenConfig;

    public TokenPair handle(String name, String email, String password)
        throws UserAlreadyExistsException
    {
        User user = this.userRepository.findByEmail(email);

        if (user != null) throw new UserAlreadyExistsException(email);

        User newUser = this.userRepository.save(User.create(
            name,
            email,
            this.passwordEncoder.encode(password),
            Role.USER
        ));

        String accessToken = this.tokenProvider.generateAccessToken(newUser);
        String rawRefreshToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.create(
            newUser.getId(),
            this.tokenHasher.hash(rawRefreshToken),
            this.tokenConfig.refreshTokenTTL()
        );

        this.refreshTokenRepository.save(refreshToken);

        return new TokenPair(accessToken, rawRefreshToken);
    }
}
