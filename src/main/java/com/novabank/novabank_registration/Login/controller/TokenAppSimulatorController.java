package com.novabank.novabank_registration.Login.controller;

import com.novabank.novabank_registration.config.OtpStore;
import com.novabank.novabank_registration.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@Profile("dev")
@RestController
@RequiredArgsConstructor
public class TokenAppSimulatorController {

    private final OtpStore otpStore;

    @GetMapping("/api/v1/dev/otp")
    public Map<String, String> otp(@RequestParam UUID otpReference) {
        return Map.of("otp", otpStore.otpFor(otpReference)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "OTP_INVALID", "OTP request not found")));
    }
}