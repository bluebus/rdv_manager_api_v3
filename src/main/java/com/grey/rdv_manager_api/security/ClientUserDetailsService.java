package com.grey.rdv_manager_api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.grey.rdv_manager_api.domain.model.Client;
import com.grey.rdv_manager_api.repository.ClientRepository;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FIX P5 — MongoDB-backed implementation of Spring Security's UserDetailsService.
 *
 * Spring Security calls loadUserByUsername() during authentication to fetch
 * the user record and verify credentials. By default, Spring uses an in-memory
 * store that knows nothing about our Client documents.
 *
 * This class replaces that default by loading the Client from MongoDB via
 * ClientRepository and converting it into a Spring Security UserDetails object.
 *
 * Wired into the AuthenticationManager in SecurityConfig via DaoAuthenticationProvider.
 */
@Service
@RequiredArgsConstructor
public class ClientUserDetailsService implements UserDetailsService {

    // ClientRepository.findByEmail() is already defined — no changes needed there
    private final ClientRepository clientRepository;

    /**
     * Called by Spring Security during the login authentication process.
     *
     * FLOW:
     *   AuthController.login()
     *     → AuthenticationManager.authenticate()
     *       → DaoAuthenticationProvider
     *         → loadUserByUsername(email)   ← this method
     *           → clientRepository.findByEmail()
     *             → returns UserDetails with hashed password + roles
     *           → DaoAuthenticationProvider.matches(rawPassword, hashedPassword)
     *             → BCrypt comparison
     *
     * @param email used as the username in this application
     * @throws UsernameNotFoundException if no Client document exists with this email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // STEP 1 — Fetch the Client document from MongoDB by email.
        // findByEmail() returns null (not Optional) per ClientRepository definition,
        // so we wrap it in Optional.ofNullable() for safe null handling.
        Client client = Optional.ofNullable(clientRepository.findByEmail(email))
                .orElseThrow(() ->
                    new UsernameNotFoundException("No client found with email: " + email));

        // STEP 2 — Convert the Client's Role enum list to Spring Security
        // GrantedAuthority objects. Each role is prefixed with "ROLE_" so that
        // Spring's hasRole("ADMIN") check (which looks for "ROLE_ADMIN") works correctly.
        var authorities = client.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        // STEP 3 — Return a Spring Security User object.
        // This is NOT our Client domain object — it is Spring's own UserDetails
        // implementation. It carries:
        //   - username  : email (the lookup key)
        //   - password  : the BCrypt hash stored in MongoDB (compared by DaoAuthenticationProvider)
        //   - authorities: the role list built above
        return new org.springframework.security.core.userdetails.User(
                client.getEmail(),
                client.getPasswordHash(),
                authorities
        );
    }
}