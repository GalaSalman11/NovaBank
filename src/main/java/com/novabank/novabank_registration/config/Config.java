package com.novabank.novabank_registration.config;

import com.novabank.novabank_registration.repository.RolesRepository;
import com.novabank.novabank_registration.entity.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Config {



    @Bean
    public CommandLineRunner seedRoles(RolesRepository roleRepository) {
        return args -> {
            List.of("CUSTOMER", "TELLER", "ADMIN").forEach(name -> {
                if (!roleRepository.existsByName(name)) {
                    Role role = new Role();
                    role.setName(name);
                    roleRepository.save(role);
                }
            });
        };
    }
}
