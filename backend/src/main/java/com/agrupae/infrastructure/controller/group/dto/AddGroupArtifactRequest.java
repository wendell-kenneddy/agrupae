package com.agrupae.infrastructure.controller.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddGroupArtifactRequest(
    @NotBlank String name,
    String description,
    @NotNull Boolean privateArtifact,
    @NotBlank String resourceLink) {
}
