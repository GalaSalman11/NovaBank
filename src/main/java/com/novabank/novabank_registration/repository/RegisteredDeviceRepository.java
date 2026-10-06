package com.novabank.novabank_registration.repository;

import com.novabank.novabank_registration.entity.RegisteredDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegisteredDeviceRepository extends JpaRepository<RegisteredDevice, UUID> {

    Optional<RegisteredDevice> findByCustomerId(UUID customerId);
}