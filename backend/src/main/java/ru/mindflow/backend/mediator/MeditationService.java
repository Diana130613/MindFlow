package ru.mindflow.backend.mediator;

import ru.mindflow.backend.dto.MeditationDto;
import java.util.List;

public interface MeditationService {
    List<MeditationDto> getAll();
    MeditationDto getById(Long id);
    List<MeditationDto> getByCategory(Long categoryId);
    List<MeditationDto> search(String query);
}