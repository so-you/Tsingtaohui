package com.tsingtaohui.common.util;

import com.tsingtaohui.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long userId, String username, String userType) {
        return generateToken(userId, username, userType, "access", jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(Long userId, String username, String userType) {
        return generateToken(userId, username, userType, "refresh", jwtProperties.getRefreshTokenExpiration());
    }

    private String generateToken(Long userId, String username, String userType, String tokenType, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(Map.of(
                        "userId", userId,
                        "username", username,
                        "userType", userType,
                        "typ", tokenType
                ))
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    public String getUserType(String token) {
        Claims claims = parseToken(token);
        return claims.get("userType", String.class);
    }

    public boolean isTokenValid(String token) {
        return isAccessToken(token);
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = parseToken(token);
            return "access".equals(claims.get("typ", String.class));
        } catch (Exception e) {
            return false;
        }
    }
}
