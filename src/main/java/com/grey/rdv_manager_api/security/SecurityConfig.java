package com.grey.rdv_manager_api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.ProviderManager;

/**
 * Master Spring Security configuration for the application.
 *
 * Defines three things:
 *   1. The HTTP security filter chain — what is public, what requires auth,
 *      what role is needed for each endpoint group.
 *   2. The PasswordEncoder bean — BCrypt, used by ClientServiceImpl on
 *      registration and by DaoAuthenticationProvider on login.
 *   3. The AuthenticationManager bean — wires ClientUserDetailsService
 *      (MongoDB-backed) into Spring Security so it replaces the default
 *      in-memory user store (FIX P5).
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Runs before UsernamePasswordAuthenticationFilter on every request
    private final JwtAuthenticationFilter jwtAuthFilter;

    // Our MongoDB-backed UserDetailsService (replaces Spring's default in-memory store)
    private final ClientUserDetailsService userDetailsService;

    /**
     * FILTER CHAIN — defines the security rules for every HTTP request.
     *
     * Processing order for an incoming request:
     *   1. CORS preflight handled by RdvManagerApi.simpleCorsFilter() (already fixed)
     *   2. CSRF disabled — not needed for stateless JWT APIs
     *   3. Session policy = STATELESS — Spring will never create an HttpSession;
     *      every request must carry its own JWT
     *   4. JwtAuthenticationFilter runs — populates SecurityContext if token is valid
     *   5. authorizeHttpRequests rules evaluated — permit or reject based on role
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — safe for stateless REST APIs that don't use browser cookies
            .csrf(csrf -> csrf.disable())

            // Never create or use an HttpSession — each request is self-contained
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // PUBLIC — login and registration require no token
                .requestMatchers("/api/auth/**").permitAll()

                // ADMIN only — full audit trail access
                .requestMatchers("/api/audit-logs/**").hasRole("ADMIN")

                // ADMIN + STAFF — client management
                .requestMatchers("/api/clients/**").hasAnyRole("ADMIN", "STAFF")

                // ADMIN only — top-level structure management
                .requestMatchers("/api/structures/**").hasRole("ADMIN")

                // ADMIN + STAFF — service and availability management
                .requestMatchers("/api/services/**").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers("/api/service-availabilities/**").hasAnyRole("ADMIN", "STAFF")

                // ADMIN + STAFF — slot management
                .requestMatchers("/api/slots/**").hasAnyRole("ADMIN", "STAFF")

                // Any authenticated user (any role) — reservations and reminders
                .requestMatchers("/api/reservations/**").authenticated()
                .requestMatchers("/api/reminders/**").authenticated()

                // Catch-all — any endpoint not listed above requires authentication
                .anyRequest().authenticated()
            )

            // Register JwtAuthenticationFilter to run BEFORE Spring's own
            // UsernamePasswordAuthenticationFilter in the chain
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCryptPasswordEncoder bean.
     * Injected into:
     *   - ClientServiceImpl  → encodes plain password on registration
     *   - AuthController     → verifies plain password against stored hash at login
     *   - DaoAuthenticationProvider (below) → used internally by Spring Security
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager bean — FIX P5.
     *
     * DaoAuthenticationProvider connects Spring Security's authentication
     * mechanism to our ClientUserDetailsService (MongoDB) and BCryptPasswordEncoder.
     *
     * Without this, Spring auto-configures an in-memory user store with a
     * random generated password printed to the console — completely bypassing
     * our Client documents and making all login attempts fail.
     */
    @Bean
    public AuthenticationManager authManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Tell Spring Security WHERE to load users from (our MongoDB Client collection)
        provider.setUserDetailsService(userDetailsService);
        // Tell Spring Security HOW to verify passwords (BCrypt)
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }
}