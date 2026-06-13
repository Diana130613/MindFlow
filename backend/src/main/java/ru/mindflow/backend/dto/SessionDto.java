package ru.mindflow.backend.dto;

import java.time.LocalDateTime;

public record SessionDto(
        Long id,
        Long meditationId,
        String meditationTitle,
        Integer durationSeconds,
        Boolean completed,
        LocalDateTime completedAt
) {}