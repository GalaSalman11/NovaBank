package com.novabank.novabank_registration.Login.controller;

import com.novabank.novabank_registration.Login.service.AdminService;
import com.novabank.novabank_registration.dto.PendingRegistrationDto;
import com.novabank.novabank_registration.dto.RejectRequestDto;
import com.novabank.novabank_registration.entity.CustomerStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/registrations")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public Page<PendingRegistrationDto> list(
            @RequestParam(defaultValue = "PENDING") CustomerStatus status,
            @PageableDefault(size = 10, sort = "pendingSince", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return adminService.list(status, pageable);
    }
    @PostMapping("/{id}/approve")
    public ResponseEntity<Map<String, String>> approve(@PathVariable UUID id, Authentication auth) {
        adminService.approve(id, auth.getName());
        return ResponseEntity.ok(Map.of("message", "Registration approved"));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Map<String, String>> reject(@PathVariable UUID id,
                                                      @Valid @RequestBody RejectRequestDto body,
                                                      Authentication auth) {
        adminService.reject(id, auth.getName(), body.reason());
        return ResponseEntity.ok(Map.of("message", "Registration rejected"));
    }
}