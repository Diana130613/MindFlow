package ru.mindflow.backend.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mindflow.backend.entity.MoodEntry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MoodEntryRepository
        extends JpaRepository<MoodEntry, Long> {

    List<MoodEntry> findByUserIdOrderByRecordedAtDesc(Long userId);

    @Query("""
        SELECT m FROM MoodEntry m
        WHERE m.user.id = :userId
        AND m.recordedAt >= :from
        ORDER BY m.recordedAt DESC
    """)
    List<MoodEntry> findByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from
    );

    @Query("""
        SELECT AVG(m.score) FROM MoodEntry m
        WHERE m.user.id = :userId
        AND m.recordedAt >= :from
    """)
    Optional<Double> getAverageScore(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from
    );

    @Query("""
        SELECT m FROM MoodEntry m
        WHERE m.user.id = :userId
        AND m.recordedAt >= :startOfDay
        AND m.recordedAt < :endOfDay
    """)
    Optional<MoodEntry> findTodayEntry(
            @Param("userId") Long userId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}