package com.novabank.novabank_registration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VerifyOtpDto(
        @NotNull(message = "otpReference is required") UUID otpReference,
        @NotBlank(message = "otp is required") String otp
) {}