package com.novabank.novabank_registration.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_sessions",
        indexes = @Index(name = "idx_session_customer_active", columnList = "customer_id, active"))
@Getter @Setter
public class UserSession {

    @Id
    @Column(name = "session_id", nullable = false, updatable = false)
    private UUID sessionId;                 // goes into the JWT as jti

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;              // whose session it is

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, updatable = false)
    private Platform platform;              // WEB / MOBILE / ATM

    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}