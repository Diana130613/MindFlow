package ru.mindflow.backend.control;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mindflow.backend.dto.ProgressDto;
import ru.mindflow.backend.dto.SessionDto;
import ru.mindflow.backend.dto.SessionRequest;
import ru.mindflow.backend.foundation.UserRepository;
import ru.mindflow.backend.mediator.SessionService;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final UserRepository userRepository;

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"))
                .getId();
    }

    @PostMapping
    public ResponseEntity<SessionDto> save(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SessionRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.save(userId, request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<SessionDto>> getMySessions(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(sessionService.getUserSessions(userId));
    }

    @GetMapping("/progress")
    public ResponseEntity<ProgressDto> getProgress(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(sessionService.getUserProgress(userId));
    }
}
