package com.wyx.demo.service;

import com.wyx.demo.dto.MenuDto;
import com.wyx.demo.dto.MenuMetaDto;
import com.wyx.demo.entity.Menu;
import com.wyx.demo.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuDto> getMenusByUsername(String username) {
        List<Menu> menus = menuRepository.findByUsernameAndParentIsNullOrderBySortOrderAsc(username);
        return menus.stream()
                .map(this::convertToDto)
                .toList();
    }

    private MenuDto convertToDto(Menu menu) {
        return MenuDto.builder()
                .name(menu.getName())
                .path(menu.getPath())
                .redirect(menu.getRedirect())
                .component(menu.getComponent())
                .meta(MenuMetaDto.builder()
                        .title(menu.getTitle())
                        .icon(menu.getIcon())
                        .order(menu.getSortOrder())
                        .affixTab(menu.getAffixTab())
                        .build())
                .children(menu.getChildren() != null && !menu.getChildren().isEmpty() ?
                        menu.getChildren().stream().map(this::convertToDto).toList() : null)
                .build();
    }
}
