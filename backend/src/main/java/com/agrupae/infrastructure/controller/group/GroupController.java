package com.agrupae.infrastructure.controller.group;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrupae.application.port.in.group.CreateGroupUseCase;
import com.agrupae.application.port.in.group.GroupView;
import com.agrupae.application.port.in.group.JoinOpenGroupUseCase;
import com.agrupae.infrastructure.controller.group.dto.CreateGroupRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}/assignments/{assignmentId}/groups")
public class GroupController {
    private final CreateGroupUseCase createGroupUseCase;
    private final JoinOpenGroupUseCase joinOpenGroupUseCase;

    @PostMapping
    public ResponseEntity<GroupView> create(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @Valid @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        GroupView view = this.createGroupUseCase.handle(
                userId,
                courseId,
                assignmentId,
                request.name(),
                request.open());

        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<Void> join(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @PathVariable UUID groupId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        this.joinOpenGroupUseCase.handle(courseId, assignmentId, groupId, userId);
        return ResponseEntity.noContent().build();
    }
}
