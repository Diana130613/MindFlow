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
    public List<Mood