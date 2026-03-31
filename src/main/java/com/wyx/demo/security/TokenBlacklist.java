package com.wyx.demo.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {

    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    public void add(String token, long expirationMillis) {
        blacklistedTokens.put(token, System.currentTimeMillis() + expirationMillis);
    }

    public boolean isBlacklisted(String token) {
        Long expiry = blacklistedTokens.get(token);
        if (expiry == null) {
            return false;
        }
        if (System.currentTimeMillis() > expiry) {
            blacklistedTokens.remove(token);
            return false;
        }
        return true;
    }
}
