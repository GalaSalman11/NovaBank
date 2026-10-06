package com.novabank.novabank_registration.Login.controller;

import com.novabank.novabank_registration.Login.service.LoginService;
import com.novabank.novabank_registration.config.OtpStore;
import com.novabank.novabank_registration.dto.LoginRequestDto;
import com.novabank.novabank_registration.dto.LoginResponseDto;
import com.novabank.novabank_registration.dto.VerifyOtpDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LoginController {


    private final LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request,
            @RequestHeader(value = "X-Channel", required = false) String channel,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId) {
        return ResponseEntity.ok(loginService.login(request, channel, deviceId));
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<LoginResponseDto> verifyOtp(
            @Valid @RequestBody VerifyOtpDto body,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId) {
        return ResponseEntity.ok(loginService.verifyOtp(body.otpReference(), deviceId, body.otp()));
    }

}