package com.wyx.demo.controller;

import com.wyx.demo.common.ApiResponse;
import com.wyx.demo.dto.MenuDto;
import com.wyx.demo.security.TokenBlacklist;
import com.wyx.demo.service.MenuService;
import com.wyx.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<MenuDto>>> getAllMenus(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null || !jwtUtil.validateToken(token) || tokenBlacklist.isBlacklisted(token)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(-1, "Unauthorized Exception"));
        }

        String username = jwtUtil.getUsernameFromToken(token);
        List<MenuDto> menus = menuService.getMenusByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(menus));
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
