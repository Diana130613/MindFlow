package ru.mindflow.backend.dto;

public record MeditationDto(
        Long id,
        String title,
        String description,
        Integer durationMinutes,
        String audioUrl,
        String imageUrl,
        String difficultyLevel,
        String categoryName
) {}