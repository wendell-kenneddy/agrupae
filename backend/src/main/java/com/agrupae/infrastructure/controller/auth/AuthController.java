package com.agrupae.infrastructure.controller.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrupae.application.port.in.authentication.LoginUseCase;
import com.agrupae.application.port.in.authentication.LogoutUseCase;
import com.agrupae.application.port.in.authentication.RefreshUseCase;
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
    private final RefreshUseCase refreshUseCase;
    private final LogoutUseCase logoutUseCase;
    private final TokenConfig tokenConfig;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        TokenPair tokenPair = this.loginUseCase.handle(
            request.email(),
            request.password()
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(tokenPair.rawRefreshToken()).toString())
                .body(tokenPair.accessToken());
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        TokenPair tokenPair = this.signupUseCase.handle(
            request.name(),
            request.email(),
            request.password()
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(tokenPair.rawRefreshToken()).toString())
                .body(tokenPair.accessToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(
            @CookieValue("refresh-token") String rawRefreshToken) {

        TokenPair tokenPair = this.refreshUseCase.handle(rawRefreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(tokenPair.rawRefreshToken()).toString())
                .body(tokenPair.accessToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue("refresh-token") String rawRefreshToken) {

        this.logoutUseCase.handle(rawRefreshToken);

        ResponseCookie clearCookie = ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .build();
    }

    private ResponseCookie buildRefreshCookie(String rawRefreshToken) {
        return ResponseCookie.from("refresh-token", rawRefreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(tokenConfig.refreshTokenTTL())
                .build();
    }
}
