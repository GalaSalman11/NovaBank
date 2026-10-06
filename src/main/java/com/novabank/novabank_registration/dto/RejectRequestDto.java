package com.novabank.novabank_registration.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectRequestDto(
        @NotBlank(message = "Reason is required") String reason
) {}