package com.wyx.demo.controller;

import com.wyx.demo.common.ApiResponse;
import com.wyx.demo.dto.UserInfoResponse;
import com.wyx.demo.security.TokenBlacklist;
import com.wyx.demo.service.UserService;
import com.wyx.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null || !jwtUtil.validateToken(token) || tokenBlacklist.isBlacklisted(token)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(-1, "Unauthorized Exception"));
        }

        String username = jwtUtil.getUsernameFromToken(token);
        UserInfoResponse userInfo = userService.getUserInfo(username);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
