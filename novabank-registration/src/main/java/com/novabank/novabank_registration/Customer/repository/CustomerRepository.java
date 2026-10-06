package com.novabank.novabank_registration.Customer.repository;

import com.novabank.novabank_registration.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface  CustomerRepository   extends JpaRepository<Customer, UUID> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMobileNumber(String mobileNumber);
}
