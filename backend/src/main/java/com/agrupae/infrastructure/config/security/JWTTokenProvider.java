package com.agrupae.infrastructure.config.security;

import java.time.Instant;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.agrupae.application.port.out.authentication.TokenProvider;
import com.agrupae.domain.user.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTTokenProvider implements TokenProvider {
    private final JwtEncoder jwtEncoder;
    private final TokenProperties tokenProperties;

    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        SignatureAlgorithm algorithm = SignatureAlgorithm.RS256;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("agrupae")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(tokenProperties.getAccessTokenTtl()))
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .build();
        JwsHeader header = JwsHeader.with(algorithm)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
