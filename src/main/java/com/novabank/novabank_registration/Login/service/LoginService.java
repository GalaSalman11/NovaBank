package com.novabank.novabank_registration.Login.service;

import com.novabank.novabank_registration.repository.CustomerRepository;
import com.novabank.novabank_registration.repository.UserSessionRepository;
import com.novabank.novabank_registration.config.OtpStore;
import com.novabank.novabank_registration.dto.LoginRequestDto;
import com.novabank.novabank_registration.dto.LoginResponseDto;
import com.novabank.novabank_registration.entity.Customer;
import com.novabank.novabank_registration.entity.CustomerStatus;
import com.novabank.novabank_registration.entity.Platform;
import com.novabank.novabank_registration.entity.UserSession;
import com.novabank.novabank_registration.exception.ApiException;
import com.novabank.novabank_registration.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserSessionRepository userSessionRepository;
    private final CustomerRepository customerRepository;
    private final DeviceService deviceService;
    private final OtpStore otpStore;

    // STEP 1: credentials. WEB/ATM get the JWT now; MOBILE gets an OTP request.
    @Transactional
    public LoginResponseDto login(LoginRequestDto request, String channelHeader, String deviceId) {

        Platform platform = parsePlatform(channelHeader);

        // password first: wrong password or unknown email throws BadCredentialsException (401)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Customer customer = customerRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // status only after the password is proven
        switch (customer.getStatus()) {
            case ACTIVE -> { }
            case PENDING -> throw new ApiException(HttpStatus.FORBIDDEN,
                    "ACCOUNT_PENDING_APPROVAL", "Your account is awaiting approval");
            case REJECTED -> throw new ApiException(HttpStatus.FORBIDDEN,
                    "ACCOUNT_REJECTED", "Your application was declined");
            case SUSPENDED -> throw new ApiException(HttpStatus.FORBIDDEN,
                    "ACCOUNT_SUSPENDED", "Your account is suspended");
            default -> throw new ApiException(HttpStatus.FORBIDDEN,
                    "ACCOUNT_NOT_ACTIVE", "Your account cannot log in");
        }

        if (platform == Platform.MOBILE) {
            if (deviceId == null || deviceId.isBlank()) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "DEVICE_REQUIRED", "X-Device-Id is required for MOBILE");
            }
            UUID reference = otpStore.create(customer.getId(), deviceId.trim());
            return LoginResponseDto.otpRequired(reference);      // no session yet
        }

        return issueSession(customer, platform);
    }

    // STEP 2 (MOBILE only): OTP verification
    @Transactional
    public LoginResponseDto verifyOtp(UUID otpReference, String deviceId, String otp) {
        OtpStore.OtpEntry e = otpStore.find(otpReference)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,
                        "OTP_INVALID", "OTP request not found"));

        if (e.expiresAt().isBefore(Instant.now())) {
            otpStore.remove(otpReference);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "OTP_EXPIRED", "The OTP has expired");
        }
        if (deviceId == null || !e.deviceId().equals(deviceId.trim())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "DEVICE_MISMATCH",
                    "This OTP was issued for a different device");
        }
        if (otp == null || !MessageDigest.isEqual(
                e.otp().getBytes(StandardCharsets.UTF_8), otp.getBytes(StandardCharsets.UTF_8))) {
            otpStore.recordFailure(otpReference);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "OTP_INVALID", "The OTP is wrong");
        }
        otpStore.remove(otpReference);                           // single use

        Customer customer = customerRepository.findById(e.customerId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,
                        "OTP_INVALID", "OTP request not found"));
        if (customer.getStatus() != CustomerStatus.ACTIVE) {     // may have changed meanwhile
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "ACCOUNT_NOT_ACTIVE", "Your account cannot log in");
        }

        deviceService.bindOrReplace(customer, e.deviceId());     // after full authentication
        return issueSession(customer, Platform.MOBILE);
    }

    // shared by both paths: one active session, then the JWT
    private LoginResponseDto issueSession(Customer customer, Platform platform) {
        userSessionRepository.deactivateAllForCustomer(customer.getId());

        Instant now = Instant.now();
        UserSession session = new UserSession();
        session.setSessionId(UUID.randomUUID());
        session.setCustomer(customer);
        session.setPlatform(platform);
        session.setIssuedAt(now);
        session.setExpiresAt(now.plus(JwtUtil.SESSION_TTL));
        session.setActive(true);
        userSessionRepository.save(session);

        String token = jwtUtil.generateToken(customer.getEmail(),
                List.of("ROLE_" + customer.getRole().getName()), session);
        return LoginResponseDto.authenticated(token, session.getExpiresAt(), platform);
    }

    private Platform parsePlatform(String header) {
        if (header == null || header.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "CHANNEL_REQUIRED", "X-Channel header is required");
        }
        try {
            return Platform.valueOf(header.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "CHANNEL_INVALID", "Unknown platform");
        }
    }

    @Transactional
    public void logout(UUID sessionId) {
        userSessionRepository.findById(sessionId).ifPresent(s -> s.setActive(false));
    }
}