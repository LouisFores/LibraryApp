package com.example.librarybackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    // ⚠ Secret phải >= 32 ký tự cho HS256
    private static final String SECRET =
            "9sd8f7s9df8s7df9s8df7s9df8s7df9s8df7s9df";

    private static final long EXPIRATION_SECONDS = 60 * 60 * 24; // 24h

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String username, String role) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("role", role))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(EXPIRATION_SECONDS)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}