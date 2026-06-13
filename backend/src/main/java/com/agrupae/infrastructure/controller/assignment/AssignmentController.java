package com.agrupae.infrastructure.controller.assignment;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.agrupae.application.port.in.assignment.AddReferenceArtifactUseCase;
import com.agrupae.application.port.in.assignment.AssignmentArtifactView;
import com.agrupae.application.port.in.assignment.ArchiveAssignmentUseCase;
import com.agrupae.application.port.in.assignment.AssignmentView;
import com.agrupae.application.port.in.assignment.CreateAssignmentUseCase;
import com.agrupae.application.port.in.assignment.GetAssignmentArtifactsUseCase;
import com.agrupae.infrastructure.controller.assignment.dto.AddReferenceArtifactRequest;
import com.agrupae.application.port.in.assignment.EditAssignmentUseCase;
import com.agrupae.domain.role.Role;
import com.agrupae.infrastructure.controller.assignment.dto.CreateAssignmentRequest;
import com.agrupae.infrastructure.controller.assignment.dto.EditAssignmentRequest;
import com.agrupae.application.port.in.assignment.GetAssignmentsUseCase;
import com.agrupae.application.port.in.assignment.GetAnAssignmentUseCase;
import com.agrupae.application.port.in.group.GetUserGroupEntryRequestsUseCase;
import com.agrupae.application.port.in.group.GroupEntryRequestView;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}/assignments")
public class AssignmentController {
    private final CreateAssignmentUseCase createAssignmentUseCase;
    private final AddReferenceArtifactUseCase addReferenceArtifactUseCase;
    private final GetAssignmentArtifactsUseCase getAssignmentArtifactsUseCase;
    private final ArchiveAssignmentUseCase archiveAssignmentUseCase;
    private final EditAssignmentUseCase editAssignmentUseCase;
    private final GetAssignmentsUseCase getAssignmentsUseCase;
    private final GetAnAssignmentUseCase getAnAssignmentUseCase;
    private final GetUserGroupEntryRequestsUseCase getUserGroupEntryRequestsUseCase;

    @GetMapping
    public ResponseEntity<Page<AssignmentView>> getAssignments(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Page<AssignmentView> view = this.getAssignmentsUseCase.handle(userId, courseId, pageable);
        return ResponseEntity.ok(view);
    }

    @PostMapping
    public ResponseEntity<AssignmentView> create(
            @PathVariable UUID courseId,
            @Valid @RequestBody CreateAssignmentRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        AssignmentView view = this.createAssignmentUseCase.handle(
                userId,
                courseId,
                request.name(),
                request.description(),
                request.dueDate(),
                request.assignmentFlags());

        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentView> getAssignment(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        AssignmentView view = this.getAnAssignmentUseCase.handle(userId, courseId, assignmentId);
        return ResponseEntity.ok(view);
    }

    @PutMapping("/{assignmentId}")
    public ResponseEntity<AssignmentView> edit(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @Valid @RequestBody EditAssignmentRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID actorId = UUID.fromString(jwt.getSubject());
        Role actorRole = Role.valueOf(jwt.getClaimAsString("role"));
        AssignmentView view = this.editAssignmentUseCase.handle(
                actorId,
                actorRole,
                courseId,
                assignmentId,
                request.name(),
                request.description(),
                request.dueDate(),
                request.assignmentFlags());
        return ResponseEntity.ok(view);
    }

    @PostMapping("/{assignmentId}/archive")
    public ResponseEntity<Void> archive(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID actorId = UUID.fromString(jwt.getSubject());
        Role actorRole = Role.valueOf(jwt.getClaimAsString("role"));
        this.archiveAssignmentUseCase.handle(actorId, actorRole, courseId, assignmentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{assignmentId}/artifacts")
    public ResponseEntity<AssignmentArtifactView> addArtifact(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @Valid @RequestBody AddReferenceArtifactRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        AssignmentArtifactView view = this.addReferenceArtifactUseCase.handle(
                userId,
                courseId,
                assignmentId,
                request.name(),
                request.description(),
                request.resourceLink());
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @GetMapping("/{assignmentId}/artifacts")
    public ResponseEntity<List<AssignmentArtifactView>> getArtifacts(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<AssignmentArtifactView> view = this.getAssignmentArtifactsUseCase.handle(
                userId,
                courseId,
                assignmentId);
        return ResponseEntity.status(HttpStatus.OK).body(view);
    }

    @GetMapping("/{assignmentId}/entry-requests/me")
    public ResponseEntity<List<GroupEntryRequestView>> getMyEntryRequests(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<GroupEntryRequestView> view = this.getUserGroupEntryRequestsUseCase.handle(
                courseId,
                assignmentId,
                userId);
        return ResponseEntity.ok(view);
    }
}
