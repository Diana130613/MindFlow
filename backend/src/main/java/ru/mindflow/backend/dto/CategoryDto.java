package ru.mindflow.backend.dto;

public record CategoryDto(
        Long id,
        String name,
        String description,
        String iconUrl
) {}