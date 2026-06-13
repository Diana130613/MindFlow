package ru.mindflow.backend.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindflow.backend.dto.ProgressDto;
import ru.mindflow.backend.dto.SessionDto;
import ru.mindflow.backend.dto.SessionRequest;
import ru.mindflow.backend.entity.*;
import ru.mindflow.backend.foundation.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final MeditationSessionRepository sessionRepository;
    private final MeditationRepository meditationRepository;
    private final UserRepository userRepository;
    private final UserProgressRepository progressRepository;

    @Override
    @Transactional
    public SessionDto save(Long userId, SessionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Meditation meditation = meditationRepository.findById(request.meditationId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Медитация не найдена: " + request.meditationId()));

        MeditationSession session = MeditationSession.builder()
                .user(user)
                .meditation(meditation)
                .durationSeconds(request.durationSeconds())
                .completed(request.completed())
                .completedAt(request.completed() ? LocalDateTime.now() : null)
                .build();

        MeditationSession saved = sessionRepository.save(session);

        // Обновляем прогресс пользователя при завершённой сессии
        if (Boolean.TRUE.equals(request.completed())) {
            UserProgress progress = progressRepository.findByUserId(userId)
                    .orElseGet(() -> UserProgress.builder().user(user)
                            .totalSessions(0).totalMinutes(0)
                            .currentStreak(0).longestStreak(0).build());
            progress.addSession(request.durationSeconds());
            progressRepository.save(progress);
        }

        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionDto> getUserSessions(Long userId) {
        return sessionRepository.findByUserIdOrderByCompletedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressDto getUserProgress(Long userId) {
        return progressRepository.findByUserId(userId)
                .map(p -> new ProgressDto(
                        p.getTotalSessions(),
                        p.getTotalMinutes(),
                        p.getCurrentStreak(),
                        p.getLongestStreak()))
                .orElse(new ProgressDto(0, 0, 0, 0));
    }

    private SessionDto toDto(MeditationSession s) {
        return new SessionDto(
                s.getId(),
                s.getMeditation().getId(),
                s.getMeditation().getTitle(),
                s.getDurationSeconds(),
                s.getCompleted(),
                s.getCompletedAt()
        );
    }
}