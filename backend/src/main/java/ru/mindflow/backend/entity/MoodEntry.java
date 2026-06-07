package ru.mindflow.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mood_entries")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoodEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }

    // Бизнес-метод Entity-слоя
    public String getMoodLabel() {
        return switch (score) {
            case 9, 10 -> "Отлично";
            case 7, 8  -> "Хорошо";
            case 5, 6  -> "Нормально";
            case 3, 4  -> "Плохо";
            default    -> "Очень плохо";
        };
    }
}