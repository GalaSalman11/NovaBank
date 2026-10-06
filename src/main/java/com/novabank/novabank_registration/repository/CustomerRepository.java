package com.novabank.novabank_registration.repository;

import com.novabank.novabank_registration.entity.Customer;
import com.novabank.novabank_registration.entity.CustomerStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface  CustomerRepository   extends JpaRepository<Customer, UUID> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMobileNumber(String mobileNumber);

    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);
    Optional<Customer> findByEmailIgnoreCase(String email);

}
