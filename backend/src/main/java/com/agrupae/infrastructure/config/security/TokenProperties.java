package com.agrupae.infrastructure.config.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.agrupae.application.port.out.authentication.TokenConfig;

import lombok.Getter;

@Component
@Getter
public class TokenProperties implements TokenConfig {
    @Value("${jwt.access-token.ttl}")
    private long accessTokenTtl;
    @Value("${jwt.refresh-token.ttl}")
    private long refreshTokenTtl;

    @Override
    public Duration refreshTokenTTL() {
        return Duration.ofSeconds(this.refreshTokenTtl);
    }
}
