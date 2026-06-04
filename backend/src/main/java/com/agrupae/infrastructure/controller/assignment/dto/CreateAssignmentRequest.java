package com.agrupae.infrastructure.controller.assignment.dto;

import java.time.Instant;

import com.agrupae.domain.assignment.AssignmentFlags;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAssignmentRequest(
    @NotBlank String name,
    @NotBlank String description,
    @NotNull Instant dueDate,
    @NotNull AssignmentFlags assignmentFlags) {

}
