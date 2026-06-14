package com.grey.rdv_manager_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Central JWT utility — responsible for THREE things only:
 *   1. Building (signing) a token at login
 *   2. Parsing (verifying) a token on every incoming request
 *   3. Extracting claims (email, roles) from a verified token
 *
 * It is a @Component so Spring can inject it wherever needed
 * (JwtAuthenticationFilter, AuthController).
 */
@Component
public class JwtTokenProvider {

    // SecretKey is built ONCE at startup from the base64 value in application.yml
    // (app.jwt.secret). Storing it as a field avoids re-decoding on every request.
    private final SecretKey key;

    // How long a token stays valid — read from app.jwt.expiration-ms in application.yml
    private final long expirationMs;

    /**
     * Constructor injection reads both values from application.yml:
     *   app.jwt.secret          → base64-encoded HMAC-SHA256 key (min 32 chars decoded)
     *   app.jwt.expiration-ms   → token lifetime in milliseconds (e.g. 86400000 = 24h)
     */
    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.expirationMs = expirationMs;
    }

    /**
     * STEP — Login success path.
     * Called by AuthController once credentials are verified.
     * Builds a signed JWT containing:
     *   - subject  : the client's email (used as the username)
     *   - roles    : comma-separated role string, e.g. "ADMIN,STAFF"
     *   - issuedAt : current timestamp
     *   - expiration: issuedAt + expirationMs
     *
     * The token is signed with HMAC-SHA256 using the secret key.
     * Tampering with the payload will invalidate the signature.
     */
    public String generateToken(String email, String roles) {
        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * STEP — Protected request path.
     * Parses and verifies the token signature.
     * Returns the Claims object (payload) if valid.
     * Throws JwtException if the token is expired, malformed, or tampered with.
     *
     * Called internally by validateToken() and getEmailFromToken().
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the email (subject) from a verified token.
     * Called by JwtAuthenticationFilter to identify the requesting user.
     */
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Safe wrapper around getClaims() — returns true if the token is
     * valid and unexpired, false for any failure (expired, bad signature,
     * malformed, null). Used by JwtAuthenticationFilter as the gate check.
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Intentionally swallowed — invalid tokens are simply ignored,
            // not thrown, so the filter chain continues unauthenticated.
            return false;
        }
    }
}