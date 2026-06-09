package ru.mindflow.backend.mediator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mindflow.backend.dto.MeditationDto;
import ru.mindflow.backend.entity.Category;
import ru.mindflow.backend.entity.Meditation;
import ru.mindflow.backend.foundation.MeditationRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeditationServiceImplTest {

    @Mock private MeditationRepository meditationRepo;
    @InjectMocks private MeditationServiceImpl service;

    private Meditation meditation(Long id, String title, Category category) {
        return Meditation.builder()
                .id(id).title(title).description("Описание")
                .durationMinutes(10).audioUrl("audio.mp3").imageUrl("img.png")
                .difficultyLevel("beginner").active(true).category(category)
                .build();
    }

    @Test
    void getAll_returnsMappedList() {
        Category cat = Category.builder().id(1L).name("Сон").build();
        when(meditationRepo.findByActiveTrue()).thenReturn(List.of(
                meditation(1L, "Медитация 1", cat),
                meditation(2L, "Медитация 2", null)
        ));

        List<MeditationDto> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("Сон", result.get(0).categoryName());
        assertNull(result.get(1).categoryName());
    }

    @Test
    void getAll_returnsEmptyList() {
        when(meditationRepo.findByActiveTrue()).thenReturn(List.of());
        assertTrue(service.getAll().isEmpty());
    }

    @Test
    void getById_returnsDtoWhenFound() {
        Category cat = Category.builder().id(2L).name("Стресс").build();
        when(meditationRepo.findById(1L)).thenReturn(Optional.of(meditation(1L, "Дыхание", cat)));

        MeditationDto result = service.getById(1L);

        assertEquals(1L, result.id());
        assertEquals("Дыхание", result.title());
        assertEquals("Стресс", result.categoryName());
    }

    @Test
    void getById_throwsWhenNotFound() {
        when(meditationRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.getById(99L));
    }

    @Test
    void getByCategory_returnsMappedList() {
        when(meditationRepo.findByCategoryIdAndActiveTrue(1L)).thenReturn(List.of(
                meditation(1L, "Сон 1", null),
                meditation(2L, "Сон 2", null)
        ));
        assertEquals(2, service.getByCategory(1L).size());
    }

    @Test
    void getByCategory_returnsEmptyList() {
        when(meditationRepo.findByCategoryIdAndActiveTrue(99L)).thenReturn(List.of());
        assertTrue(service.getByCategory(99L).isEmpty());
    }

    @Test
    void search_returnsMatchingMeditations() {
        when(meditationRepo.searchByQuery("сон")).thenReturn(
                List.of(meditation(1L, "Медитация для сна", null))
        );

        List<MeditationDto> result = service.search("сон");

        assertEquals(1, result.size());
        assertEquals("Медитация для сна", result.get(0).title());
    }

    @Test
    void search_returnsEmptyWhenNoMatches() {
        when(meditationRepo.searchByQuery("xyz")).thenReturn(List.of());
        assertTrue(service.search("xyz").isEmpty());
    }

    @Test
    void toDto_mapsAllFields() {
        Category cat = Category.builder().id(1L).name("Фокус").build();
        Meditation m = Meditation.builder()
                .id(10L).title("Концентрация").description("Описание практики")
                .durationMinutes(15).audioUrl("audio.mp3").imageUrl("img.png")
                .difficultyLevel("intermediate").active(true).category(cat)
                .build();
        when(meditationRepo.findById(10L)).thenReturn(Optional.of(m));

        MeditationDto dto = service.getById(10L);

        assertEquals(10L, dto.id());
        assertEquals("Концентрация", dto.title());
        assertEquals("Описание практики", dto.description());
        assertEquals(15, dto.durationMinutes());
        assertEquals("audio.mp3", dto.audioUrl());
        assertEquals("img.png", dto.imageUrl());
        assertEquals("intermediate", dto.difficultyLevel());
        assertEquals("Фокус", dto.categoryName());
    }

    @Test
    void toDto_withNullCategory_returnsDtoWithNullCategoryName() {
        Meditation m = meditation(5L, "Без категории", null);
        when(meditationRepo.findById(5L)).thenReturn(Optional.of(m));

        MeditationDto dto = service.getById(5L);
        assertNull(dto.categoryName());
    }
}
