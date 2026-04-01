package com.wyx.demo.service;

import com.wyx.demo.dto.LoginRequest;
import com.wyx.demo.dto.LoginResponse;
import com.wyx.demo.entity.User;
import com.wyx.demo.repository.UserRepository;
import com.wyx.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public record LoginResult(LoginResponse response, User user) {}

    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .filter(u -> u.getPassword().equals(request.getPassword()))
                .orElse(null);

        if (user == null) {
            return null;
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRoles());

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .build();

        return new LoginResult(loginResponse, user);
    }

    public record RefreshResult(String accessToken, String refreshToken) {}

    public RefreshResult refresh(String refreshToken) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return null;
        }

        if (!"refresh".equals(jwtUtil.parseToken(refreshToken).get("type", String.class))) {
            return null;
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return null;
        }

        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRoles());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        return new RefreshResult(newAccessToken, newRefreshToken);
    }
}
