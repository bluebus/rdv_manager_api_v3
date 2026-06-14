package com.grey.rdv_manager_api.payload.response;

import com.grey.rdv_manager_api.domain.enums.Role;
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
public record AuthResponse(

    // The signed JWT to verify the duration set in app.jwt.expiration-ms
    String token,

    // The authenticated client's ID 
    UUID id, 
    
    // The authenticated client's email
    String email,

    // The client's roles as plain strings, e.g. ["ADMIN", "STAFF"]
    // Matches the Role enum values defined in domain/enums/Role.java
    List<Role> roles

) {}