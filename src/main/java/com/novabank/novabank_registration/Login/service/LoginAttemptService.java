package com.novabank.novabank_registration.Login.service;

import com.novabank.novabank_registration.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration COOLDOWN = Duration.ofMinutes(5);

    private record Attempt(int count, Instant lockedUntil){}
    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public void checkNotLocked(String email) {
        Attempt a = attempts.get(email.toLowerCase());
        if (a != null && a.lockedUntil() != null && a.lockedUntil().isAfter(Instant.now())) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS,
                    "TOO_MANY_ATTEMPTS", "Too many failed attempts. Try again later.");
        }
    }

    public void recordFailure(String email) {
        attempts.merge(email.toLowerCase(), new Attempt(1, null), (old, ignored) -> {
            int count = old.count() + 1;
            return new Attempt(count, count >= MAX_ATTEMPTS ? Instant.now().plus(COOLDOWN) : null);
        });
    }

    public void recordSuccess(String email) {
        attempts.remove(email.toLowerCase());
    }
}