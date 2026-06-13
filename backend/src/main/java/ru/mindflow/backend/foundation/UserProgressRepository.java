package ru.mindflow.backend.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mindflow.backend.entity.UserProgress;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUserId(Long userId);
}