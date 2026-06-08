package com.agrupae.infrastructure.controller.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGroupRequest(
    @NotBlank String name,
    @NotNull Boolean open) {
}
