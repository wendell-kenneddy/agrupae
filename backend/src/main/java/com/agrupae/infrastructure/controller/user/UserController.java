package com.agrupae.infrastructure.controller.user;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrupae.application.port.in.user.GetUserProfileUseCase;
import com.agrupae.application.port.in.user.UpdateProfileUseCase;
import com.agrupae.application.port.in.user.UserProfileView;
import com.agrupae.infrastructure.controller.user.dto.UpdateProfileRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    @GetMapping("/me")
    public ResponseEntity<UserProfileView> getProfile(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        UserProfileView view = this.getUserProfileUseCase.handle(userId);

        return ResponseEntity.ok(view);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileView> updateProfile(@AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateProfileRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());

        UserProfileView view = this.updateProfileUseCase.handle(userId, request.name(), request.email());

        return ResponseEntity.ok(view);
    }

}
