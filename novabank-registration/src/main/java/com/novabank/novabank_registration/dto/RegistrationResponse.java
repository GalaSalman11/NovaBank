package com.novabank.novabank_registration.dto;

import com.novabank.novabank_registration.entity.CustomerStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record RegistrationResponse(
        UUID customerId,
        String firstName,
        String lastName,
        String email,
        CustomerStatus status,
        Instant registeredAt
) {
}