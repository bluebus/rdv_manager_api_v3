package com.grey.rdv_manager_api.payload.request;

import jakarta.validation.constraints.*;


public record CreateStructureRequest(
/*@NotBlank(message = "Name is required") String name,
        String description,
@NotBlank(message = "Address is required") String address,
@NotBlank(message = "Phone is required") @Pattern(regexp = "\\+?[0-9.-]{7,15}", message = "Invalid phone format") String phone,
@NotBlank @Email(message = "Invalid email") String email,
@NotBlank(message = "Timezone is required") String timezone
*/

        // 20260606 update the data verification field

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotBlank(message = "Address is required")
        @Size(max = 200, message = "Address must not exceed 200 characters")
        String address,

        @NotBlank(message = "Phone is required")
        @Pattern(
                regexp = "\\+?[0-9.\\-]{7,15}",
                message = "Phone must be 7–15 digits, e.g. +60123456789"
        )
        String phone,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        // Validates Region/City format e.g. Asia/Kuala_Lumpur
        @NotBlank(message = "Timezone is required")
        @Pattern(
                regexp = "^[A-Za-z]+/[A-Za-z_]+$",
                message = "Timezone must be in Region/City format, e.g. Asia/Kuala_Lumpur"
        )
        String timezone
        ){}
