package com.agrupae.infrastructure.controller.group.dto;

import jakarta.validation.constraints.NotBlank;

public record EditGroupRequest(
    @NotBlank String name
) {
}
