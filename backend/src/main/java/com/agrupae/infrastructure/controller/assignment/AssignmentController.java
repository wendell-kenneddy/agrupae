package com.agrupae.infrastructure.controller.assignment;

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

import com.agrupae.application.port.in.assignment.AddReferenceArtifactUseCase;
import com.agrupae.application.port.in.assignment.AssignmentArtifactView;
import com.agrupae.application.port.in.assignment.AssignmentView;
import com.agrupae.application.port.in.assignment.CreateAssignmentUseCase;
import com.agrupae.infrastructure.controller.assignment.dto.AddReferenceArtifactRequest;
import com.agrupae.infrastructure.controller.assignment.dto.CreateAssignmentRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}/assignments")
public class AssignmentController {
    private final CreateAssignmentUseCase createAssignmentUseCase;
    private final AddReferenceArtifactUseCase addReferenceArtifactUseCase;

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

    @PostMapping("/{assignmentId}/artifacts")
    public ResponseEntity<AssignmentArtifactView> addArtifact(
            @PathVariable UUID courseId,
            @PathVariable UUID assignmentId,
            @Valid @RequestBody AddReferenceArtifactRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        AssignmentArtifactView view = this.addReferenceArtifactUseCase.handle(
                userId,
                assignmentId,
                request.name(),
                request.description(),
                request.resourceLink());
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

}
