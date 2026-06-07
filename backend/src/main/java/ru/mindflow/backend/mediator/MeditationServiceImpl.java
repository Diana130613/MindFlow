package ru.mindflow.backend.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindflow.backend.dto.MeditationDto;
import ru.mindflow.backend.entity.Meditation;
import ru.mindflow.backend.foundation.MeditationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeditationServiceImpl implements MeditationService {

    private final MeditationRepository meditationRepository;

    @Override
    public List<MeditationDto> getAll() {
        return meditationRepository.findByActiveTrue()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public MeditationDto getById(Long id) {
        return meditationRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() ->
                        new IllegalArgumentException("Медитация не найдена: " + id));
    }

    @Override
    public List<MeditationDto> getByCategory(Long categoryId) {
        return meditationRepository
                .findByCategoryIdAndActiveTrue(categoryId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<MeditationDto> search(String query) {
        return meditationRepository.searchByQuery(query)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private MeditationDto toDto(Meditation m) {
        return new MeditationDto(
                m.getId(),
                m.getTitle(),
                m.getDescription(),
                m.getDurationMinutes(),
                m.getAudioUrl(),
                m.getImageUrl(),
                m.getDifficultyLevel(),
                m.getCategory() != null ? m.getCategory().getName() : null
        );
    }
}