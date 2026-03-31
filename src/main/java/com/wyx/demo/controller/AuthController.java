package com.wyx.demo.controller;

import com.wyx.demo.common.ApiResponse;
import com.wyx.demo.dto.LoginRequest;
import com.wyx.demo.dto.LoginResponse;
import com.wyx.demo.service.AuthService;
import com.wyx.demo.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    private static final String COOKIE_NAME = "jwt";
    private static final int COOKIE_MAX_AGE = 24 * 60 * 60; // 1天

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        AuthService.LoginResult loginResult = authService.login(request);

        if (loginResult == null) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(-1, "Username or password is incorrect."));
        }

        // 生成 refreshToken 并设置 Cookie
        String refreshToken = jwtUtil.generateRefreshToken(
                loginResult.getUser().getId(), loginResult.getUser().getUsername());
        setCookie(response, refreshToken);

        return ResponseEntity.ok(ApiResponse.success(loginResult.getResponse()));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletResponse response) {
        clearCookie(response);
        return ApiResponse.success("");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookie(request);

        if (refreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(-1, "Forbidden Exception"));
        }

        String newAccessToken = authService.refresh(refreshToken);
        if (newAccessToken == null) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(-1, "Forbidden Exception"));
        }

        // 重新设置 Cookie
        setCookie(response, refreshToken);

        // 直接返回 accessToken 字符串，不包装
        return ResponseEntity.ok(newAccessToken);
    }

    private String getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setCookie(HttpServletResponse response, String value) {
        response.addHeader("Set-Cookie",
                String.format("%s=%s; Path=/; HttpOnly; Secure; SameSite=none; Max-Age=%d",
                        COOKIE_NAME, value, COOKIE_MAX_AGE));
    }

    private void clearCookie(HttpServletResponse response) {
        response.addHeader("Set-Cookie",
                String.format("%s=; Path=/; HttpOnly; Secure; SameSite=none; Max-Age=0", COOKIE_NAME));
    }
}
