package com.novabank.novabank_registration.Login.service;

import com.novabank.novabank_registration.repository.CustomerRepository;
import com.novabank.novabank_registration.dto.PendingRegistrationDto;
import com.novabank.novabank_registration.entity.Customer;
import com.novabank.novabank_registration.entity.CustomerStatus;
import com.novabank.novabank_registration.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final CustomerRepository customerRepository;

    //    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);
    @Transactional(readOnly = true)
    public Page<PendingRegistrationDto> list(CustomerStatus status, Pageable pageable) {
        return customerRepository.findByStatus(status, pageable)
                .map(c -> new PendingRegistrationDto(
                        c.getId(), c.getFirstName(), c.getLastName(),
                        c.getEmail(), c.getStatus(), c.getPendingSince()));
    }


    @Transactional
    public void approve(UUID customerId, String officerEmail) {
        review(customerId, CustomerStatus.ACTIVE, officerEmail, null);
    }

    @Transactional
    public void reject(UUID customerId, String officerEmail, String reason) {
        review(customerId, CustomerStatus.REJECTED, officerEmail, reason.trim());
    }

    // the ONLY place that decides whether a status change is allowed
    private void review(UUID customerId, CustomerStatus target, String officerEmail, String reason) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "CUSTOMER_NOT_FOUND", "Registration not found"));

        if (customer.getEmail().equalsIgnoreCase(officerEmail)) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "SELF_REVIEW_NOT_ALLOWED", "You cannot review your own registration");
        }

        if (customer.getStatus() != CustomerStatus.PENDING) {
            throw new ApiException(HttpStatus.CONFLICT, "INVALID_STATUS_TRANSITION",
                    "Cannot change status from " + customer.getStatus() + " to " + target);
        }

        customer.setStatus(target);
        customer.setReviewedBy(officerEmail);
        customer.setReviewedAt(Instant.now());
        customer.setRejectionReason(reason);
    }
}
