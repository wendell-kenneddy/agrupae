package com.agrupae.infrastructure.controller.group.dto;

import jakarta.validation.constraints.NotNull;

public record ChangeGroupModeRequest(
    @NotNull Boolean open) {
}
