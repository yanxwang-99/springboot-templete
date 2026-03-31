package com.wyx.demo.service;

import com.wyx.demo.dto.LoginRequest;
import com.wyx.demo.dto.LoginResponse;
import com.wyx.demo.entity.User;
import com.wyx.demo.repository.UserRepository;
import com.wyx.demo.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Getter
    @AllArgsConstructor
    public static class LoginResult {
        private final LoginResponse response;
        private final User user;
    }

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

    public String refresh(String refreshToken) {
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

        return jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRoles());
    }
}
