package ru.mindflow.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SessionRequest(
        @NotNull Long meditationId,
        @NotNull @Min(0) Integer durationSeconds,
        @NotNull Boolean completed
) {}
