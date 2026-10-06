package com.novabank.novabank_registration.Register.controller;

import com.novabank.novabank_registration.Register.Service.RegistrationService;
import com.novabank.novabank_registration.dto.RegistrationRequest;
import com.novabank.novabank_registration.dto.RegistrationResponse;
import com.novabank.novabank_registration.entity.Customer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest request) {
        Customer saved = registrationService.registerCustomer(request);
        RegistrationResponse response = toResponseDto(saved);

        return ResponseEntity
                .created(URI.create("/api/v1/auth/customers/" + saved.getId()))
                .body(response);
    }

    private RegistrationResponse toResponseDto(Customer customer) {
        return new RegistrationResponse(
                customer.getId(),
                customer.getEmail(),
                customer.getStatus(),
                customer.getPendingSince(),
                "Your registration is awaiting approval by a bank officer");
    }
}
