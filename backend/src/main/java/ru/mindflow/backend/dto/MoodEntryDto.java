package ru.mindflow.backend.dto;

import java.time.LocalDateTime;

public record MoodEntryDto(
        Long id,
        Integer score,
        String note,
        String moodLabel,
        LocalDateTime recordedAt
) {}