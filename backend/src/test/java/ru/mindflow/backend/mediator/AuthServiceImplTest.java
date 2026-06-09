package ru.mindflow.backend.mediator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mindflow.backend.dto.AuthResponse;
import ru.mindflow.backend.dto.LoginRequest;
import ru.mindflow.backend.dto.RegisterRequest;
import ru.mindflow.backend.entity.User;
import ru.mindflow.backend.foundation.UserRepository;
import ru.mindflow.backend.security.JwtUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepo;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private AuthServiceImpl service;

    private User user() {
        return User.builder()
                .id(1L).email("test@test.com").name("Тест")
                .password("encoded_pass").role(User.Role.ROLE_USER)
                .build();
    }

    @Test
    void register_returnsAuthResponseWhenEmailFree() {
        when(userRepo.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Pass123")).thenReturn("encoded_pass");
        when(userRepo.save(any())).thenReturn(user());
        when(jwtUtil.generateAccessToken(any())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh_token");

        AuthResponse result = service.register(
                new RegisterRequest("Тест", "new@test.com", "Pass123"));

        assertNotNull(result);
        assertEquals("access_token", result.accessToken());
        assertEquals("refresh_token", result.refreshToken());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void register_throwsWhenEmailAlreadyTaken() {
        when(userRepo.existsByEmail("taken@test.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> service.register(new RegisterRequest("Тест", "taken@test.com", "Pass123")));
        verify(userRepo, never()).save(any());
    }

    @Test
    void login_returnsAuthResponseWithValidCredentials() {
        User user = user();
        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass123", "encoded_pass")).thenReturn(true);
        when(jwtUtil.generateAccessToken(any())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh_token");

        AuthResponse result = service.login(new LoginRequest("test@test.com", "Pass123"));

        assertNotNull(result);
        assertEquals("test@test.com", result.email());
        assertEquals("Тест", result.name());
    }

    @Test
    void login_throwsWhenUserNotFound() {
        when(userRepo.findByEmail("noone@test.com")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.login(new LoginRequest("noone@test.com", "pass")));
    }

    @Test
    void login_throwsWhenWrongPassword() {
        User user = user();
        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_pass", "encoded_pass")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.login(new LoginRequest("test@test.com", "wrong_pass")));
    }

    @Test
    void refresh_returnsNewTokensWhenValid() {
        User user = user();
        when(jwtUtil.isValid("valid_token")).thenReturn(true);
        when(jwtUtil.extractEmail("valid_token")).thenReturn("test@test.com");
        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(any())).thenReturn("new_access");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("new_refresh");

        AuthResponse result = service.refresh("valid_token");

        assertEquals("new_access", result.accessToken());
        assertEquals("new_refresh", result.refreshToken());
    }

    @Test
    void refresh_throwsWhenTokenInvalid() {
        when(jwtUtil.isValid("bad_token")).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> service.refresh("bad_token"));
    }
}
