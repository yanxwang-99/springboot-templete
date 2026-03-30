package com.wyx.demo.repository;

import com.wyx.demo.entity.UserCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCodeRepository extends JpaRepository<UserCode, Long> {
    List<UserCode> findByUsername(String username);
}
