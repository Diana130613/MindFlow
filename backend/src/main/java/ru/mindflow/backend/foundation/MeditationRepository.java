package ru.mindflow.backend.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mindflow.backend.entity.Meditation;
import java.util.List;

public interface MeditationRepository
        extends JpaRepository<Meditation, Long> {

    List<Meditation> findByActiveTrue();

    List<Meditation> findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("""
        SELECT m FROM Meditation m
        WHERE m.active = true
        AND (LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(m.description) LIKE LOWER(CONCAT('%', :q, '%')))
    """)
    List<Meditation> searchByQuery(@Param("q") String query);
}