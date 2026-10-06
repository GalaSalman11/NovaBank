package com.novabank.novabank_registration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "registered_devices",
        uniqueConstraints = @UniqueConstraint(columnNames = "customer_id"))   // one device per customer
@Getter
@Setter
public class RegisteredDevice {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "bound_at", nullable = false)
    private Instant boundAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;
}