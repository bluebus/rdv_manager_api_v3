package com.grey.rdv_manager_api.payload.request;

import jakarta.validation.constraints.*;
import com.grey.rdv_manager_api.domain.enums.Role;
import java.util.Set;


public record UpdateClientRequest(
    /*String firstName,
    String lastName,
    @Email String email,
    @Pattern(regexp = "^[0-9+\\s-]{6,20}$") String phone,
    String password, // assume optional update
    Set<String> roles // must match enum values
    */

    // 20260606 update the data verification field
    @Size(max = 50, message = "First name must not exceed 50 characters")
    String firstName,

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    String lastName,

    @Email(message = "Email must be a valid email address")
    String email,

    @Pattern(
        regexp = "^(\\+?[0-9.\\-\\s]{6,20})?$",
        message = "Phone must be 6–20 digits, e.g. +60123456789"
    )
    String phone,

    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,

    Set<Role> roles

) {}

