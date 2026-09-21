package com.novabank.novabank_registration.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorDto(String apiPath, String errorCode, String errorMessage,
                       LocalDateTime errorTime) {
}
