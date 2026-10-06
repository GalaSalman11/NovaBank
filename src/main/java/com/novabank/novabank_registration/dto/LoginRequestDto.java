package com.novabank.novabank_registration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record LoginRequestDto(

        @NotBlank(message = "Email Name cannot be Empty")
             String email,


        @NotBlank(message = "Password cannot be Empty")
        String password
) implements Serializable {
}
