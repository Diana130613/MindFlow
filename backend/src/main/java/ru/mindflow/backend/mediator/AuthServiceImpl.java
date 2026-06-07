package ru.mindflow.backend.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mindflow.backend.dto.*;
import ru.mindflow.backend.entity.User;
import ru.mindflow.backend.foundation.UserRepository;
import ru.mindflow.backend.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email уже зарегистрирован: " + request.email()
            );
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(User.Role.ROLE_USER)
                .build();

        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException("Неверный email или пароль"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Неверный email или пароль");
        }

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        if (!jwtUtil.isValid(refreshToken)) {
            throw new IllegalArgumentException("Невалидный refresh token");
        }
        String email = jwtUtil.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Пользователь не найден"));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return new AuthResponse(
                jwtUtil.generateAccessToken(user.getEmail()),
                jwtUtil.generateRefreshToken(user.getEmail()),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}