package com.wyx.demo.controller;

import com.wyx.demo.common.ApiResponse;
import com.wyx.demo.dto.MenuDto;
import com.wyx.demo.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<MenuDto>>> getAllMenus(@AuthenticationPrincipal String username) {
        List<MenuDto> menus = menuService.getMenusByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(menus));
    }
}
