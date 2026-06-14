package com.grey.rdv_manager_api.payload.request;

import jakarta.validation.constraints.*;

public record UpdateServiceRequest(
    /*String name,
    String description,
    String timezone*/

    // 20260606 update the data verification field
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description
) {}

