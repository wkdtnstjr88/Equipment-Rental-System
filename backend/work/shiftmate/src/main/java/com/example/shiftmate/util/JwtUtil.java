package com.example.shiftmate.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    // JWT Token 生成
    public String generateToken(Long userNumber, String userId, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userNumber", userNumber);
        claims.put("userId", userId);
        claims.put("userType", userType);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Tokenからユーザー番号を出す(추출)
    public Long getUserNumberFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userNumber", Long.class);
    }

    // TokenからユーザーIDを出す
    public String getUserIdFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Tokenからユーザータイプを出す
    public String getUserTypeFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userType", String.class);
    }

    // Token 有効性検証
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Token 満了確認（토큰 만료확인)
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // 全てのClaimsを出す
    private Claims extractAllClaims(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
