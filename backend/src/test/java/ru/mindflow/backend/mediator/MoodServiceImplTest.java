package ru.mindflow.backend.mediator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mindflow.backend.dto.MoodEntryDto;
import ru.mindflow.backend.dto.MoodEntryRequest;
import ru.mindflow.backend.entity.MoodEntry;
import ru.mindflow.backend.entity.User;
import ru.mindflow.backend.foundation.MoodEntryRepository;
import ru.mindflow.backend.foundation.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoodServiceImplTest {

    @Mock private MoodEntryRepository moodRepo;
    @Mock private UserRepository userRepo;
    @InjectMocks private MoodServiceImpl service;

    private User user() {
        return User.builder()
                .id(1L).email("test@test.com").name("Тест")
                .role(User.Role.ROLE_USER).build();
    }

    private MoodEntry entry(Long id, int score, String note, User user) {
        return MoodEntry.builder()
                .id(id).score(score).note(note).user(user)
                .recordedAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .build();
    }

    @Test
    void save_returnsDtoWithCorrectFields() {
        User user = user();
        MoodEntry saved = entry(1L, 7, "Хороший день", user);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(moodRepo.save(any())).thenReturn(saved);

        MoodEntryDto result = service.save(1L, new MoodEntryRequest(7, "Хороший день"));

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(7, result.score());
        assertEquals("Хороший день", result.note());
        assertEquals("Хорошо", result.moodLabel());
        verify(moodRepo).save(any(MoodEntry.class));
    }

    @Test
    void save_throwsWhenUserNotFound() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.save(99L, new MoodEntryRequest(5, null)));
        verify(moodRepo, never()).save(any());
    }

    @Test
    void save_withNullNote_returnsDto() {
        User user = user();
        MoodEntry saved = entry(2L, 5, null, user);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(moodRepo.save(any())).thenReturn(saved);

        MoodEntryDto result = service.save(1L, new MoodEntryRequest(5, null));
        assertNull(result.note());
        assertEquals(5, result.score());
    }

    @Test
    void getHistory_returnsMappedList() {
        User user = user();
        when(moodRepo.findByUserIdAndPeriod(eq(1L), any())).thenReturn(List.of(
                entry(1L, 8, "OK", user),
                entry(2L, 6, "Fine", user)
        ));

        List<MoodEntryDto> result = service.getHistory(1L, 7);

        assertEquals(2, result.size());
        assertEquals(8, result.get(0).score());
        assertEquals(6, result.get(1).score());
    }

    @Test
    void getHistory_returnsEmptyList() {
        when(moodRepo.findByUserIdAndPeriod(eq(1L), any())).thenReturn(List.of());
        assertTrue(service.getHistory(1L, 7).isEmpty());
    }

    @Test
    void getToday_returnsDtoWhenEntryExists() {
        User user = user();
        when(moodRepo.findTodayEntry(eq(1L), any(), any()))
                .thenReturn(Optional.of(entry(1L, 9, "Отлично", user)));

        MoodEntryDto result = service.getToday(1L);

        assertNotNull(result);
        assertEquals(9, result.score());
        assertEquals("Отлично", result.moodLabel());
    }

    @Test
    void getToday_returnsNullWhenNoEntry() {
        when(moodRepo.findTodayEntry(eq(1L), any(), any())).thenReturn(Optional.empty());
        assertNull(service.getToday(1L));
    }

    @Test
    void delete_successWhenOwner() {
        User user = user();
        MoodEntry entry = entry(5L, 7, "ok", user);
        when(moodRepo.findById(5L)).thenReturn(Optional.of(entry));

        service.delete(5L, 1L);

        verify(moodRepo).delete(entry);
    }

    @Test
    void delete_throwsWhenEntryNotFound() {
        when(moodRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.delete(99L, 1L));
    }

    @Test
    void delete_throwsWhenNotOwner() {
        User user = user();
        MoodEntry entry = entry(5L, 7, "ok", user);
        when(moodRepo.findById(5L)).thenReturn(Optional.of(entry));

        assertThrows(IllegalArgumentException.class,
                () -> service.delete(5L, 999L));
        verify(moodRepo, never()).delete(any());
    }

    @Test
    void getAverage_returnsValue() {
        when(moodRepo.getAverageScore(eq(1L), any())).thenReturn(Optional.of(7.5));
        assertEquals(7.5, service.getAverage(1L, 7));
    }

    @Test
    void getAverage_returnsZeroWhenNoEntries() {
        when(moodRepo.getAverageScore(eq(1L), any())).thenReturn(Optional.empty());
        assertEquals(0.0, service.getAverage(1L, 7));
    }
}