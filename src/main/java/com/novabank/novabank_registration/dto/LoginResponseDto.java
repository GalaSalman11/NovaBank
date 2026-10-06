package com.novabank.novabank_registration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.novabank.novabank_registration.entity.Platform;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponseDto(String status, UUID otpReference, String token,
                               Instant expiresAt, Platform platform) {

    public static LoginResponseDto authenticated(String token, Instant expiresAt, Platform platform) {
        return new LoginResponseDto("AUTHENTICATED", null, token, expiresAt, platform);
    }
    public static LoginResponseDto otpRequired(UUID otpReference) {
        return new LoginResponseDto("OTP_REQUIRED", otpReference, null, null, null);
    }
}