package com.grey.rdv_manager_api.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for POST /api/auth/login.
 *
 * Intentionally minimal — only the two fields needed to authenticate.
 * Validation annotations ensure neither field is submitted blank and
 * that the email follows a valid format before the handler method runs.
 *
 * Kept separate from CreateClientRequest (registration) so that the
 * login contract stays stable even if registration fields change.
 */
public record LoginRequest(

    // Must be a syntactically valid email address — used as the username lookup key
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    String email,

    // The plain-text password submitted by the user.
    // It is NEVER stored — AuthController passes it to BCrypt.matches()
    // for comparison against the stored hash, then discards it.
    @NotBlank(message = "Password is required")
    String password

) {}