package com.grey.rdv_manager_api.payload.response;

import com.grey.rdv_manager_api.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Response body returned by POST /api/auth/login on success.
 *
 * The client (frontend / mobile app) should:
 *   1. Store the token securely (e.g. memory or httpOnly cookie — NOT localStorage)
 *   2. Attach it to every subsequent request as:
 *        Authorization: Bearer <token>
 *   3. Use the roles list to control UI visibility (which menu items to show etc.)
 *      — but NEVER trust roles from this response for actual access decisions;
 *        that enforcement happens server-side in SecurityConfig.
 *
 * No sensitive data (passwordHash, internal IDs beyond email) is included here.
 */
@Schema(description = "Returned on successful login")
public record AuthResponse(

    // The signed JWT to verify the duration set in app.jwt.expiration-ms
    @Schema(description = "JWT Bearer token — paste into the Authorize dialog", example = "eyJhbGciOiJIUzI1NiJ9...")
    String token,

    // The authenticated client's ID 
    @Schema(description = "Logged-in client UUID — used for by-client reservation queries")
    UUID id, 
    
    // The authenticated client's email
    @Schema(description = "Email address", example = "admin@rdv.com")
    String email,

    // The client's roles as plain strings, e.g. ["ADMIN"] or ["CLIENT"]
    // Matches the Role enum values defined in domain/enums/Role.java
    @Schema(description = "Roles assigned to this account")
    List<Role> roles

) {}