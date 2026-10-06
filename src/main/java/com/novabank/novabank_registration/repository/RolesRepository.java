package com.novabank.novabank_registration.repository;

import com.novabank.novabank_registration.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RolesRepository extends JpaRepository<Role, UUID> {


    boolean existsByName(String name);

    Optional<Role> findByName(String name);
}
