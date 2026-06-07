package ru.mindflow.backend.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mindflow.backend.entity.MeditationSession;
import java.util.List;

public interface MeditationSessionRepository
        extends JpaRepository<MeditationSession, Long> {

    List<MeditationSession> findByUserIdOrderByCompletedAtDesc(Long userId);

    long countByUserId(Long userId);
}