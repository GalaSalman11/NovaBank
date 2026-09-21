package com.novabank.novabank_registration.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "ROLES")
@Getter
@Setter

public class Role extends  BaseEntity{
@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "NAME" , nullable = false,length = 20)
    private String name ;

}
