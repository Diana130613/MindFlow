package ru.mindflow.backend.dto;

import jakarta.validation.constraints.*;

public record MoodEntryRequest(
        @NotNull @Min(1) @Max(10) Integer score,
        String note
) {}