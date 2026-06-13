package ru.mindflow.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_progress")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Column(name = "total_sessions", nullable = false)
    private Integer totalSessions = 0;

    @Builder.Default
    @Column(name = "total_minutes", nullable = false)
    private Integer totalMinutes = 0;

    @Builder.Default
    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak = 0;

    @Builder.Default
    @Column(name = "longest_streak", nullable = false)
    private Integer longestStreak = 0;

    @Column(name = "last_session_date")
    private LocalDate lastSessionDate;

    // Бизнес-метод: добавить сессию и пересчитать стрик
    public void addSession(int durationSeconds) {
        this.totalSessions++;
        this.totalMinutes += durationSeconds / 60;

        LocalDate today = LocalDate.now();
        if (lastSessionDate == null) {
            currentStreak = 1;
        } else if (lastSessionDate.plusDays(1).equals(today)) {
            currentStreak++;
        } else if (!lastSessionDate.equals(today)) {
            currentStreak = 1;
        }

        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
        }
        lastSessionDate = today;
    }
}
