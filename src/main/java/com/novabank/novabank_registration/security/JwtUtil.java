package com.novabank.novabank_registration.security;

import javax.crypto.SecretKey;

import com.novabank.novabank_registration.entity.UserSession;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    public static final Duration SESSION_TTL = Duration.ofMinutes(15);   // LOG-101 AC9
    private final SecretKey secretKey;

    public JwtUtil(Environment env) {
        String secret = env.getProperty("JWT_SECRET");
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET must be set and at least 32 bytes");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, List<String> roles, UserSession session) {
        return Jwts.builder()
                .issuer("NovaBank")
                .subject(email)
                .id(session.getSessionId().toString())
                .claim("platform", session.getPlatform().name())
                .claim("roles", roles)
                .issuedAt(Date.from(session.getIssuedAt()))
                .expiration(Date.from(session.getExpiresAt()))
                .signWith(secretKey)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload();
    }
}