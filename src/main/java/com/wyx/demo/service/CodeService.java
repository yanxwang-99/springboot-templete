package com.wyx.demo.service;

import com.wyx.demo.entity.UserCode;
import com.wyx.demo.repository.UserCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeService {

    private final UserCodeRepository userCodeRepository;

    public List<String> getCodesByUsername(String username) {
        return userCodeRepository.findByUsername(username).stream()
                .map(UserCode::getCode)
                .toList();
    }
}
