package com.grey.rdv_manager_api.payload.request;

import jakarta.validation.constraints.*;

public record UpdateStructureRequest(
    /*String name,
    String description,
    String address,
    @Pattern(regexp = "^[0-9+\\s-]{6,20}$") String phone,
    @Email String email,
    String timezone*/

    // 20260606 update the data verification field
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,

    @Size(max = 200, message = "Address must not exceed 200 characters")
    String address,

    @Pattern(
        regexp = "^(\\+?[0-9.\\-\\s]{6,20})?$",
        message = "Phone must be 6–20 digits, e.g. +60123456789"
    )
    String phone,

    @Email(message = "Email must be a valid email address")
    String email
) {}
