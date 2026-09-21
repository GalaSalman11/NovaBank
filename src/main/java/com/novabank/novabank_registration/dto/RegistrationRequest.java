package com.novabank.novabank_registration.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;

public record RegistrationRequest(
//, firstName, lastName, email, mobileNumber, passwordHas

       @NotBlank(message = "First_Name cannot be Empty")
       @Size(min = 5, max = 30, message = "Name must be between 5 and 30 ")
        String firstName  ,

       @NotBlank(message = "Second Name cannot be Empty")
       @Size(min = 5, max = 30, message = "Name must be between 5 and 30")
        String lastName ,

       @NotBlank(message = "Email Name cannot be Empty")
       @Email(message = "Invalid email address")
        String email  ,

       @Size(min = 5, max = 30, message = "Mobile Number must be between 5 and 30")
       @NotBlank(message = "Mobile Number cannot be Empty")
        String mobileNumber  ,


       @Size( min = 5, max = 30, message = "Password must be between 5 and 30")
       @NotBlank(message = "Password cannot be Empty")
       @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d).+", message = "password must contain a letter and a digit")
        String password ,

       @Size( min = 5, max = 30, message = "Password must be between 5 and 30")
       @NotBlank(message = "Password cannot be Empty")
               String confirmPassword





) implements Serializable {

}
