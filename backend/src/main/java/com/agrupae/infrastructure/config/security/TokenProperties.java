package com.agrupae.infrastructure.config.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.agrupae.application.port.out.authentication.TokenConfig;

@ConfigurationProperties(prefix = "jwt")
public record TokenProperties(long accessTokenTtl, long refreshTokenTtl) implements TokenConfig {

    @Override
    public Duration refreshTokenTTL() {
        return Duration.ofSeconds(refreshTokenTtl);
    }
}
