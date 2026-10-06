package com.novabank.novabank_registration.Register.Service;

import com.novabank.novabank_registration.repository.CustomerRepository;
import com.novabank.novabank_registration.repository.RolesRepository;
import com.novabank.novabank_registration.dto.RegistrationRequest;
import com.novabank.novabank_registration.entity.Customer;
import com.novabank.novabank_registration.entity.CustomerStatus;
import com.novabank.novabank_registration.entity.Role;
import com.novabank.novabank_registration.exception.DuplicateEmailException;
import com.novabank.novabank_registration.exception.DuplicateMobileException;
import com.novabank.novabank_registration.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final CustomerRepository customerRepository;
    private final RolesRepository roleRepository;
    private final PasswordEncoder passwordEncoder;



    public Customer registerCustomer(RegistrationRequest request) {
        String email = request.email().trim().toLowerCase();

        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException("EMAIL_ALREADY_REGISTERED");
        }
        if (customerRepository.existsByMobileNumber(request.mobileNumber())) {

            throw new DuplicateMobileException("MOBILE_ALREADY_REGISTERED");
        }
        if(!request.password().equals(request.confirmPassword())){
            throw new PasswordMismatchException("PASSWORD_MISMATCH");
        }

        Customer customer = toEntity(request);
        return customerRepository.save(customer);
    }


    public Customer toEntity(RegistrationRequest registration) {

        Customer customer = new Customer();

        customer.setFirstName(registration.firstName());
        customer.setLastName(registration.lastName());
        customer.setEmail(registration.email().trim().toLowerCase());
        customer.setMobileNumber(registration.mobileNumber());
        customer.setPasswordHash(
                passwordEncoder.encode(registration.password())
        );
        customer.setStatus(CustomerStatus.PENDING);
        customer.setPendingSince(Instant.now());
        customer.setCreatedBy("System");

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("CUSTOMER role is not seeded"));
        customer.setRole(customerRole);



        return customer;
    }





}
