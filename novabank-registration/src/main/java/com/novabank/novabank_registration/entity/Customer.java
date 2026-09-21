package com.novabank.novabank_registration.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;


@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID )
  @Column(name = "ID", nullable = false)
  private UUID id;

    @Column(name = "FIRST_NAME", nullable = false)
  private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "EMAIL", nullable = false,unique = true)
    private String email;

    @Column(name = "MOBILE_NUMBER", nullable = false,unique = true)
    private String mobileNumber;


    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private CustomerStatus status =CustomerStatus.PENDING_VERIFICATION ;



    @OneToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "role_id", nullable = false, unique = true)
    private Role role;

    }
