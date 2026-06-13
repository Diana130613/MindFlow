package ru.mindflow.backend.dto;

public record ProgressDto(
        Integer totalSessions,
        Integer totalMinutes,
        Integer currentStreak,
        Integer longestStreak
) {}
