package com.novabank.novabank_registration.repository;

import com.novabank.novabank_registration.entity.Customer;
import com.novabank.novabank_registration.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession,UUID> {
    @Modifying(clearAutomatically = true)
    @Query("update UserSession s set s.active = false where s.customer.id = :customerId and s.active = true")
    int deactivateAllForCustomer(@Param("customerId") UUID customerId);



}
