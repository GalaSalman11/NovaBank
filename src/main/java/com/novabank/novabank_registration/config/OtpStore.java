package com.novabank.novabank_registration.config;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OtpStore {

    public record OtpEntry(UUID customerId, String deviceId, String otp, Instant expiresAt, int attempts) {}

    private static final int MAX_ATTEMPTS = 3;
    private final Map<UUID, OtpEntry> entries = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public UUID create(UUID customerId, String deviceId) {
        UUID reference = UUID.randomUUID();
        String otp = String.format("%06d", random.nextInt(1_000_000));
        entries.put(reference, new OtpEntry(customerId, deviceId, otp, Instant.now().plusSeconds(120), 0));
        return reference;
    }

    public Optional<OtpEntry> find(UUID reference) { return Optional.ofNullable(entries.get(reference)); }

    public void remove(UUID reference) { entries.remove(reference); }
    public Optional<String> otpFor(UUID reference) {
        return Optional.ofNullable(entries.get(reference)).map(OtpEntry::otp);
    }

    public void recordFailure(UUID reference) {
        entries.computeIfPresent(reference, (k, e) -> e.attempts() + 1 >= MAX_ATTEMPTS
                ? null
                : new OtpEntry(e.customerId(), e.deviceId(), e.otp(), e.expiresAt(), e.attempts() + 1));
    }
}