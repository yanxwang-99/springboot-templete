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

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .filter(u -> u.getPassword().equals(request.getPassword()))
                .orElse(null);

        if (user == null) {
            return null;
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRoles());

        return LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .realName(user.getRealName())
                .roles(user.getRoles())
                .accessToken(accessToken)
                .build();
    }

    public String refresh(String refreshToken) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
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
