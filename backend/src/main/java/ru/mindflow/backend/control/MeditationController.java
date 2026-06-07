package ru.mindflow.backend.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mindflow.backend.dto.MeditationDto;
import ru.mindflow.backend.mediator.MeditationService;

import java.util.List;

@RestController
@RequestMapping("/api/meditations")
@RequiredArgsConstructor
public class MeditationController {

    private final MeditationService meditationService;

    @GetMapping
    public ResponseEntity<List<MeditationDto>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(meditationService.search(search));
        }
        if (categoryId != null) {
            return ResponseEntity.ok(
                    meditationService.getByCategory(categoryId)
            );
        }
        return ResponseEntity.ok(meditationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeditationDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(meditationService.getById(id));
    }
}