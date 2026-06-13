package ru.mindflow.backend.mediator;

import ru.mindflow.backend.dto.*;
import java.util.List;

public interface MoodService {
    MoodEntryDto save(Long userId, MoodEntryRequest request);
    MoodEntryDto update(Long id, Long userId, MoodEntryRequest request);
    List<MoodEntryDto> getHistory(Long userId, int days);
    MoodEntryDto getToday(Long userId);
    void delete(Long id, Long userId);
    Double getAverage(Long userId, int days);
}