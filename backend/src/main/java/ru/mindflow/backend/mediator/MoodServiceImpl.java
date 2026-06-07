package ru.mindflow.backend.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindflow.backend.dto.*;
import ru.mindflow.backend.entity.*;
import ru.mindflow.backend.foundation.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoodServiceImpl implements MoodService {

    private final MoodEntryRepository moodEntryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MoodEntryDto save(Long userId, MoodEntryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Пользователь не найден"));

        MoodEntry entry = MoodEntry.builder()
                .score(request.score())
                .note(request.note())
                .user(user)
                .build();

        return toDto(moodEntryRepository.save(entry));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoodEntryDto> getHistory(Long userId, int days) {
        LocalDateTime from = LocalDateTime.now().minusDays(days);
        return moodEntryRepository
                .findByUserIdAndPeriod(userId, from)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MoodEntryDto getToday(Long userId) {
        return moodEntryRepository.findTodayEntry(userId)
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        MoodEntry entry = moodEntryRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Запись не найдена: " + id));

        if (!entry.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "Нет прав для удаления этой записи"
            );
        }

        moodEntryRepository.delete(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverage(Long userId, int days) {
        LocalDateTime from = LocalDateTime.now().minusDays(days);
        return moodEntryRepository
                .getAverageScore(userId, from)
                .orElse(0.0);
    }

    private MoodEntryDto toDto(MoodEntry entry) {
        return new MoodEntryDto(
                entry.getId(),
                entry.getScore(),
                entry.getNote(),
                entry.getMoodLabel(),
                entry.getRecordedAt()
        );
    }
}