package com.wyx.demo.controller;

import com.wyx.demo.common.ApiResponse;
import com.wyx.demo.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;

    @GetMapping("/codes")
    public ResponseEntity<ApiResponse<List<String>>> getCodes(@AuthenticationPrincipal String username) {
        List<String> codes = codeService.getCodesByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(codes));
    }
}
