package com.agrupae.infrastructure.controller.assignment.dto;

import jakarta.validation.constraints.NotBlank;

public record AddReferenceArtifactRequest(
        @NotBlank String name,
        String description,
        @NotBlank String resourceLink) {
}
