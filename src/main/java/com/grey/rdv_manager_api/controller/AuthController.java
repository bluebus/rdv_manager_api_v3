package com.grey.rdv_manager_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.grey.rdv_manager_api.domain.model.Client;
import com.grey.rdv_manager_api.payload.request.CreateClientRequest;
import com.grey.rdv_manager_api.payload.request.LoginRequest;
import com.grey.rdv_manager_api.payload.response.AuthResponse;
import com.grey.rdv_manager_api.payload.response.ClientResponse;
import com.grey.rdv_manager_api.repository.ClientRepository;
import com.grey.rdv_manager_api.security.JwtTokenProvider;
import com.grey.rdv_manager_api.service.ClientService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Public authentication endpoints — both routes are permitted without a token
 * (configured in SecurityConfig: .requestMatchers("/api/auth/**").permitAll()).
 *
 * TWO endpoints:
 *   POST /api/auth/register  → create a new Client account
 *   POST /api/auth/login     → verify credentials and return a JWT
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // Used to look up the Client document by email at login time
    private final ClientRepository clientRepository;

    // Delegates registration to the existing service layer (which now hashes the password)
    private final ClientService clientService;

    // Used at login to compare the submitted plain password against the stored BCrypt hash
    private final PasswordEncoder passwordEncoder;

    // Generates the signed JWT once credentials are verified
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * REGISTER — POST /api/auth/register
     *
     * Delegates entirely to ClientService.create() which:
     *   1. Maps CreateClientRequest → Client entity (via ClientMapper)
     *   2. Encodes the plain password with BCrypt (added in ClientServiceImpl fix)
     *   3. Saves the Client document to MongoDB
     *   4. Returns a ClientResponse (no password hash exposed)
     *
     * Returns 200 OK with the created client details (no token issued here —
     * the user must call /login separately after registering).
     */
    @PostMapping("/register")
    public ResponseEntity<ClientResponse> register(
            @Validated @RequestBody CreateClientRequest request) {
        return ResponseEntity.ok(clientService.create(request));
    }

    /**
     * LOGIN — POST /api/auth/login
     *
     * FLOW:
     *   1. Look up the Client document by email in MongoDB
     *   2. If not found → 401 (same response as wrong password to prevent user enumeration)
     *   3. BCrypt.matches(plainPassword, storedHash) — verify the password
     *   4. If mismatch → 401
     *   5. Build a comma-separated roles string from the Client's role list
     *   6. Generate a signed JWT containing email + roles
     *   7. Return AuthResponse { token, email, roles }
     *
     * The returned token must be included in subsequent requests as:
     *   Authorization: Bearer <token>
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Validated @RequestBody LoginRequest request) {

        // STEP 1 — Fetch Client by email.
        // findByEmail() returns null when not found, so wrap with Optional.
        Client client = Optional.ofNullable(clientRepository.findByEmail(request.email()))
                .orElse(null);

        // STEP 2 + 3 — Reject if client doesn't exist OR password doesn't match.
        // Both cases return 401 with an empty body — no detail about which check failed
        // (prevents an attacker from using error differences to enumerate valid emails).
        if (client == null ||
                !passwordEncoder.matches(request.password(), client.getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }

        // STEP 4 — Build the roles string for embedding in the JWT claim.
        // e.g. Role.ADMIN + Role.STAFF → "ADMIN,STAFF"
        // JwtAuthenticationFilter will split this back out on subsequent requests.
        String roles = client.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));

        // STEP 5 — Generate the signed JWT with email as subject and roles as a claim
        String token = jwtTokenProvider.generateToken(client.getEmail(), roles);

        // STEP 6 — Return the token along with email and roles list for the client app
        return ResponseEntity.ok(new AuthResponse(
                token,
                client.getId(),
                client.getEmail(),
                client.getRoles().stream().map(Enum::name).toList()
        ));
    }
}