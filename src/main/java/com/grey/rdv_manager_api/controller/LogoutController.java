package com.grey.rdv_manager_api.controller;

import com.grey.rdv_manager_api.domain.model.TokenBlacklist;
import com.grey.rdv_manager_api.repository.TokenBlacklistRepository;
import com.grey.rdv_manager_api.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * STEP 4 — NEW FILE
 * Handles user logout by revoking the current JWT token.
 *
 * Flow:
 * 1. Client sends POST /api/auth/logout with Bearer token in Authorization header.
 *    NOTE: This endpoint requires a valid token (not in permitAll in SecurityConfig).
 *          Spring Security will reject the request with 401 before it reaches here
 *          if no valid token is present.
 * 2. This controller extracts the raw token from the Authorization header.
 * 3. Saves it to MongoDB token_blacklist collection with its expiry date.
 * 4. Returns 200 OK — client then clears sessionStorage.
 *
 * After logout, JwtAuthenticationFilter checks the blacklist on every request
 * and rejects the old token with 401 even if it hasn't expired yet.
 */
@Tag(name = "Authentication", description = "Register and login — no token required")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LogoutController {

    private static final Logger log = LoggerFactory.getLogger(LogoutController.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Operation(
            summary = "Logout — revoke current token",
            description = """
                    Adds the caller's JWT to the blacklist. The token is rejected on all \
                    subsequent requests until it naturally expires, even if the expiry hasn't \
                    passed yet.
                    
                    **How to test in Swagger UI:**
                    1. Call POST /api/auth/login and copy the token from the response.
                    2. Click the **Authorize** button (top right) and paste the token.
                    3. Call this endpoint — you should get 200 OK.
                    4. Try any protected endpoint (e.g. GET /api/clients) — you should now get 401.
                    5. Check MongoDB: db.token_blacklist.find() should show one document.
                    
                    **How to test in Postman:**
                    1. POST /api/auth/login → copy token.
                    2. POST /api/auth/logout with header: Authorization: Bearer <token> → 200 OK.
                    3. GET /api/clients with same header: Authorization: Bearer <token> → 401.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token revoked successfully.",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Logged out successfully\"}"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No valid Bearer token provided — request was rejected by Spring Security before reaching this method.",
                    content = @Content(schema = @Schema(example = "{\"error\": \"Unauthorized\"}"))
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {

        // Step A: Read the Authorization header.
        // At this point, Spring Security has already validated the token —
        // this endpoint is NOT in permitAll, so an invalid/missing token
        // would have been rejected with 401 before reaching here.
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Step B: Strip "Bearer " prefix to get the raw JWT string.
            String token = authHeader.substring(7);

            // Step C: Double-check token validity before saving.
            // This is a safety guard — Spring Security already did this,
            // but we validate again to be safe before writing to the DB.
            if (jwtTokenProvider.validateToken(token)) {

                // Step D: Check if already blacklisted (e.g. duplicate logout call).
                // Avoids writing duplicate entries — the unique index on token
                // would throw anyway, but this gives a cleaner log message.
                if (tokenBlacklistRepository.existsByToken(token)) {
                    log.info("Token already blacklisted — duplicate logout call ignored.");
                    return ResponseEntity.ok(Map.of("message", "Already logged out"));
                }

                // Step E: Build the blacklist entry.
                // expiresAt is read from the token's own "exp" claim.
                // MongoDB's TTL index deletes this document automatically at that time.
                TokenBlacklist entry = TokenBlacklist.builder()
                        .token(token)
                        .expiresAt(jwtTokenProvider.getExpiration(token))
                        .build();

                // Step F: Save to MongoDB token_blacklist collection.
                // After this point, JwtAuthenticationFilter will reject this token on
                // every subsequent request, even before its natural expiry time.
                TokenBlacklist saved = tokenBlacklistRepository.save(entry);
                log.info("Token blacklisted — id: {}, expires: {}", saved.getId(), saved.getExpiresAt());

            } else {
                // Should not happen — Spring Security validates before this runs.
                log.warn("validateToken() returned false inside logout — token may have expired mid-request.");
            }
        }

        // Step G: Return 200 regardless.
        // The client should always clear sessionStorage on logout.
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}