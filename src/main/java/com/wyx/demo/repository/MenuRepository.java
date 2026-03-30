package com.wyx.demo.repository;

import com.wyx.demo.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByUsername(String username);
    List<Menu> findByUsernameAndParentIsNullOrderBySortOrderAsc(String username);
}
