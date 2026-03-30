package com.wyx.demo.controller;

import com.wyx.demo.common.ApiResponse;
import com.wyx.demo.service.CodeService;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;
    private final JwtUtil jwtUtil;

    @GetMapping("/codes")
    public ResponseEntity<ApiResponse<List<String>>> getCodes(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(-1, "Unauthorized Exception"));
        }

        String username = jwtUtil.getUsernameFromToken(token);
        List<String> codes = codeService.getCodesByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(codes));
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
