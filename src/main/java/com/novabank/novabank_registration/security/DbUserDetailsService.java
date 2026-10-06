package com.novabank.novabank_registration.security;

import com.novabank.novabank_registration.repository.CustomerRepository;
import com.novabank.novabank_registration.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {
    private  final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email ) throws UsernameNotFoundException {
        Customer c = customerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

        return User.builder()
                .username(c.getEmail())
                .password(c.getPasswordHash())
                .roles(c.getRole().getName())   // becomes ROLE_CUSTOMER / ROLE_TELLER / ROLE_ADMIN
                .build();



    }
}
