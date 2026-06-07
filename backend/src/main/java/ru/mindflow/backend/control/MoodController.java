package ru.mindflow.backend.control;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mindflow.backend.dto.*;
import ru.mindflow.backend.foundation.UserRepository;
import ru.mindflow.backend.mediator.MoodService;

import java.util.List;

@RestController
@RequestMapping("/api/mood")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;
    private final UserRepository userRepository;

    // Вспомогательный метод получения userId из токена
    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new IllegalArgumentException("Пользователь не найден"))
                .getId();
    }

    @PostMapping
    public ResponseEntity<MoodEntryDto> save(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MoodEntryRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(moodService.save(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<MoodEntryDto>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "30") int days) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(moodService.getHistory(userId, days));
    }

    @GetMapping("/today")
    public ResponseEntity<MoodEntryDto> getToday(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        MoodEntryDto today = moodService.getToday(userId);
        return today != null
                ? ResponseEntity.ok(today)
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/average")
    public ResponseEntity<Double> getAverage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "30") int days) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(moodService.getAverage(userId, days));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = getUserId(userDetails);
        moodService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}