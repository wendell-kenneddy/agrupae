package com.agrupae.infrastructure.controller.group;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrupae.application.port.in.group.CreateGroupUseCase;
import com.agrupae.application.port.in.group.GroupView;
import com.agrupae.application.port.in.group.JoinOpenGroupUseCase;
import com.agrupae.application.port.in.group.RequestGroupEntryUseCase;
import com.agrupae.application.port.in.group.CancelGroupEntryRequestUseCase;
import com.agrupae.application.port.in.group.AcceptGroupEntryRequestUseCase;
import com.agrupae.application.port.in.group.RejectGroupEntryRequestUseCase;
import com.agrupae.application.port.in.group.GetGroupEntryRequestsUseCase;
import com.agrupae.application.port.in.group.GroupEntryRequestView;
import com.agrupae.application.port.in.group.ChangeGroupModeUseCase;
import com.agrupae.domain.group.GroupEntryRequestStatus;
import com.agrupae.infrastructure.controller.group.dto.ChangeGroupModeRequest;
import com.agrupae.infrastructure.controller.group.dto.CreateGroupRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}/assignments/{assignmentId}/groups")
public class GroupController {
    private final CreateGroupUseCase createGroupUseCase;
    private final JoinOpenGroupUseCase joinOpenGroupUseCase;
    private final RequestGroupEntryUseCase requestGroupEntryUseCase;
    private final CancelGroupEntryRequestUseCase cancelGroupEntryRequestUseCase;
    private final AcceptGroupEntryRequestUseCase acceptGroupEntryRequestUseCase;
    private final RejectGroupEntryRequestUseCase rejectGroupEntryRequestUseCase;
    private final GetGroupEntryRequestsUseCase getGroupEntryRequestsUseCase;
    private final ChangeGroupModeUseCase changeGroupModeUseCase;

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

    @PostMapping("/{groupId}/entry-requests")
    public ResponseEntity<GroupEntryRequestView> requestEntry(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @PathVariable UUID groupId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        GroupEntryRequestView view = this.requestGroupEntryUseCase.handle(courseId, assignmentId, groupId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @DeleteMapping("/{groupId}/entry-requests/{requestId}")
    public ResponseEntity<Void> cancelEntryRequest(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @PathVariable UUID groupId,
            @PathVariable UUID requestId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        this.cancelGroupEntryRequestUseCase.handle(courseId, assignmentId, groupId, requestId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/entry-requests")
    public ResponseEntity<List<GroupEntryRequestView>> getEntryRequests(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @PathVariable UUID groupId,
            @RequestParam(required = false) GroupEntryRequestStatus status,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<GroupEntryRequestView> views = this.getGroupEntryRequestsUseCase.handle(
                courseId, assignmentId, groupId, userId, status);
        return ResponseEntity.ok(views);
    }

    @PostMapping("/{groupId}/entry-requests/{requestId}/accept")
    public ResponseEntity<Void> acceptEntryRequest(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @PathVariable UUID groupId,
            @PathVariable UUID requestId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        this.acceptGroupEntryRequestUseCase.handle(courseId, assignmentId, groupId, requestId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{groupId}/mode")
    public ResponseEntity<Void> changeMode(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @PathVariable UUID groupId,
            @Valid @RequestBody ChangeGroupModeRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        this.changeGroupModeUseCase.handle(courseId, assignmentId, groupId, userId, request.open());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/entry-requests/{requestId}/reject")
    public ResponseEntity<GroupEntryRequestView> rejectEntryRequest(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @PathVariable UUID groupId,
            @PathVariable UUID requestId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        GroupEntryRequestView view = this.rejectGroupEntryRequestUseCase.handle(
                courseId, assignmentId, groupId, requestId, userId);
        return ResponseEntity.ok(view);
    }
}
