package com.agrupae.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.agrupae.application.port.out.authentication.*;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.application.service.authentication.*;

@Configuration
public class ApplicationServiceConfig {

    @Bean
    public LoginService loginService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider,
            TokenHasher tokenHasher,
            TokenConfig tokenConfig) {
        return new LoginService(userRepository, refreshTokenRepository,
                passwordEncoder, tokenProvider, tokenHasher, tokenConfig);
    }

    @Bean
    public SignupService signupService(
            TokenProvider tokenProvider,
            TokenHasher tokenHasher,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RefreshTokenRepository refreshTokenRepository,
            TokenConfig tokenConfig) {
        return new SignupService(tokenProvider, tokenHasher, userRepository,
                passwordEncoder, refreshTokenRepository, tokenConfig);
    }

    @Bean
    public RefreshService refreshService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            TokenProvider tokenProvider,
            TokenHasher tokenHasher,
            TokenConfig tokenConfig) {
        return new RefreshService(userRepository, refreshTokenRepository,
                tokenProvider, tokenHasher, tokenConfig);
    }

    @Bean
    public LogoutService logoutService(
            RefreshTokenRepository refreshTokenRepository,
            TokenHasher tokenHasher) {
        return new LogoutService(refreshTokenRepository, tokenHasher);
    }
}
