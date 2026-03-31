package com.wyx.demo.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:vben-admin-secret-key-must-be-at-least-256-bits-long-for-security}")
    private String secret;

    // accessToken 7天
    @Value("${jwt.access-token-expiration:604800000}")
    private long accessTokenExpiration;

    // refreshToken 30天
    @Value("${jwt.refresh-token-expiration:2592000000}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long id, String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", username);
        claims.put("roles", roles);
        return createToken(claims, accessTokenExpiration);
    }

    public String generateRefreshToken(Long id, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", username);
        claims.put("type", "refresh");
        return createToken(claims, refreshTokenExpiration);
    }

    private String createToken(Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        return parseToken(token) != null;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("username", String.class) : null;
    }

    public Long getIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("id", Long.class) : null;
    }

    public Long getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.getExpiration().getTime() : null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("roles", List.class) : null;
    }
}
