package com.agrupae.infrastructure.controller.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrupae.application.port.in.authentication.LoginUseCase;
import com.agrupae.application.port.in.authentication.SignupUseCase;
import com.agrupae.application.port.out.authentication.TokenConfig;
import com.agrupae.domain.refresh_token.TokenPair;
import com.agrupae.infrastructure.controller.auth.dto.LoginRequest;
import com.agrupae.infrastructure.controller.auth.dto.SignupRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginUseCase loginUseCase;
    private final SignupUseCase signupUseCase;
    private final TokenConfig tokenConfig;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        TokenPair tokenPair = this.loginUseCase.handle(
            request.email(),
            request.password()
        );
        ResponseCookie refreshTokenCookie = ResponseCookie.from(
            "refresh-token",
            tokenPair.rawRefreshToken()
        )
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(tokenConfig.refreshTokenTTL())
                .build();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(tokenPair.accessToken());
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        TokenPair tokenPair = this.signupUseCase.handle(
            request.name(),
            request.email(),
            request.password()
        );
        ResponseCookie refreshTokenCookie = ResponseCookie.from(
            "refresh-token",
            tokenPair.rawRefreshToken()
        )
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(tokenConfig.refreshTokenTTL())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(tokenPair.accessToken());
    }
}
