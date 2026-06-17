package com.grey.rdv_manager_api.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import com.grey.rdv_manager_api.repository.TokenBlacklistRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * HTTP filter that runs ONCE per request (OncePerRequestFilter).
 * It sits in the Spring Security filter chain BEFORE the standard
 * UsernamePasswordAuthenticationFilter (registered in SecurityConfig).
 *
 * Responsibility: read the JWT from the Authorization header,
 * validate it, and if valid — populate the SecurityContext so that
 * Spring Security treats this request as authenticated.
 *
 * If there is no token, or the token is invalid, this filter does nothing
 * and the request continues unauthenticated. Spring Security will then
 * reject it at the authorizeHttpRequests() layer in SecurityConfig.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JwtTokenProvider handles all token parsing logic
    private final JwtTokenProvider jwtTokenProvider;
    //private final UserDetailsService userDetailsService;

    // Add TokenBlacklistRepository as a constructor field
    // @RequiredArgsConstructor will inject it automatically.
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // STEP 1 — Look for the Authorization header.
        // Expected format: "Bearer <token>"
        // If the header is missing or has the wrong prefix, skip auth entirely.
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            // STEP 2 — Strip the "Bearer " prefix to get the raw token string
            String token = header.substring(7);

            // STEP 3 — Validate the token (signature + expiry check).
            // If invalid, we skip straight to chain.doFilter() below,
            // leaving the SecurityContext empty → request will be rejected.
            if (jwtTokenProvider.validateToken(token)) {

                // STEP 3B — Check if this token was revoked ──────────────
                // Even if the token is cryptographically valid, the user may have
                // logged out. Revoked tokens are stored in the token_blacklist
                // collection in MongoDB. If found, reject immediately with 401.
                if (tokenBlacklistRepository.existsByToken(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"error\": \"Token has been revoked. Please log in again.\"}"
                    );
                    return; // stop here — do NOT continue the filter chain
                }

                // STEP 4 — Extract the claims payload from the verified token
                Claims claims = jwtTokenProvider.getClaims(token);

                // STEP 5 — Get the subject (email) — used as the principal name
                String email = claims.getSubject();

                // STEP 6 — Extract roles from the "roles" claim.
                // The value is a comma-separated string: e.g. "ADMIN" or "CLIENT" or "ADMIN,CLIENT"
                // Each role is prefixed with "ROLE_" because Spring Security's
                // hasRole("ADMIN") internally checks for "ROLE_ADMIN".
                // FIX P2: previously hardcoded as "USER" — now reads real roles from token.
                String rolesStr = claims.get("roles", String.class);
                List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesStr.split(","))
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim()))
                        .toList();

                // STEP 7 — Build a Spring Security Authentication object.
                // Credentials are null because we don't need the password here —
                // the signed JWT is the proof of identity.
                var authentication = new UsernamePasswordAuthenticationToken(
                        email,      // principal (who is making the request)
                        null,       // credentials (not needed post-authentication)
                        authorities // granted roles for this request
                );

                // STEP 8 — Store the authentication in the SecurityContext.
                // This is what Spring Security checks when evaluating
                // .hasRole() / .authenticated() rules in SecurityConfig.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // STEP 9 — Always continue the filter chain regardless of auth outcome.
        // SecurityConfig's authorizeHttpRequests() rules will reject
        // unauthenticated requests at the next stage.
        chain.doFilter(request, response);
    }
}