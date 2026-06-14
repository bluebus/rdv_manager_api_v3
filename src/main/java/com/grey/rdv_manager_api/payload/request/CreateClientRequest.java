package com.grey.rdv_manager_api.payload.request;

import com.grey.rdv_manager_api.domain.enums.Role;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public record CreateClientRequest(
/*@NotBlank String firstName,
@NotBlank String lastName,
@NotBlank @Email String email,
@Pattern(regexp = "\\+?[0-9.-]{7,15}") String phone,
@NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
@NotEmpty List<String> roles,
        UUID structureId */

        // 20260606 update the data verification field
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        // Optional but must match numeric pattern if provided
        @Pattern(
                regexp = "^(\\+?[0-9.\\-\\s]{7,15})?$",
                message = "Phone must be 7–15 digits, e.g. +60123456789"
        )
        String phone,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        // Values must match Role enum — validated in ClientMapper via valueOf()
        @NotEmpty(message = "At least one role is required")
        List<Role> roles,

        // Optional — only required for ADMIN and STAFF accounts
        UUID structureId
        ){}
