package com.agrupae.infrastructure.controller.group.dto;

import jakarta.validation.constraints.NotBlank;

public record EditGroupArtifactRequest(
    @NotBlank String name,
    String description,
    @NotBlank String resourceLink) {
}
