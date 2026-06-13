package ru.mindflow.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meditation_sessions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeditationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(nullable = false)
    private Boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meditation_id", nullable = false)
    private Meditation meditation;

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }

    // Бизнес-метод: длительность в минутах для отображения
    public int getDurationMinutes() {
        return durationSeconds != null ? durationSeconds / 60 : 0;
    }
}
