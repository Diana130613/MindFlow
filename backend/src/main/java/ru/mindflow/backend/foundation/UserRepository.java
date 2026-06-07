package ru.mindflow.backend.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mindflow.backend.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}