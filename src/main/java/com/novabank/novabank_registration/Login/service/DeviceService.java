package com.novabank.novabank_registration.Login.service;

import com.novabank.novabank_registration.repository.RegisteredDeviceRepository;
import com.novabank.novabank_registration.entity.Customer;
import com.novabank.novabank_registration.entity.RegisteredDevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final RegisteredDeviceRepository deviceRepository;

    @Transactional
    public void bindOrReplace(Customer customer, String deviceId) {
        RegisteredDevice d = deviceRepository.findByCustomerId(customer.getId()).orElse(null);
        Instant now = Instant.now();

        if (d == null) {
            d = new RegisteredDevice();
            d.setId(UUID.randomUUID());
            d.setCustomer(customer);
            d.setDeviceId(deviceId);
            d.setBoundAt(now);


        } else if (!d.getDeviceId().equals(deviceId)) //replace the old one
        {
            log.info("Device replaced for customer {}", customer.getId());
            d.setDeviceId(deviceId);
            d.setBoundAt(now);
        }
        d.setLastLoginAt(now);
        deviceRepository.save(d);
    }
}