package com.novabank.novabank_registration.dto;

import com.novabank.novabank_registration.entity.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record PendingRegistrationDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        CustomerStatus status,
        Instant pendingSince
) {}