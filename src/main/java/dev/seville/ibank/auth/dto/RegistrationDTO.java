package dev.seville.ibank.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationDTO(
        @NotBlank(message = "firstname is required")
        String firstname,

        @Email(message = "email must be valid")
        @NotBlank(message = "email is required")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 6, message = "password mus be at least 6 characters")
        String password
) {}
