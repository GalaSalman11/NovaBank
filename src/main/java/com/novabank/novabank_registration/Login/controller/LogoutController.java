package com.novabank.novabank_registration.Login.controller;

import com.novabank.novabank_registration.Login.service.LoginService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.novabank.novabank_registration.security.JwtUtil;

import java.util.Map;
import java.util.UUID;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class LogoutController {
    private final JwtUtil jwtUtil;
    private final LoginService loginService;
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        Claims claims = jwtUtil.parse(authHeader.substring(7));
        loginService.logout(UUID.fromString(claims.getId()));
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}
