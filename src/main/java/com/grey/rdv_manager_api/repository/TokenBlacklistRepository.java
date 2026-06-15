package com.grey.rdv_manager_api.repository;

import com.grey.rdv_manager_api.domain.model.TokenBlacklist;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for the token_blacklist collection.
 *
 * Only one query is needed:
 * - existsByToken(token) → returns true if this token was revoked (i.e. user logged out).
 *
 * Spring Data generates the implementation automatically from the method name.
 */
public interface TokenBlacklistRepository extends MongoRepository<TokenBlacklist, String> {

    /**
     * Checks whether a given JWT string exists in the blacklist.
     * Called on every authenticated request inside JwtAuthenticationFilter.
     *
     * @param token the raw JWT string from the Authorization header
     * @return true if the token has been revoked (user logged out)
     */
    boolean existsByToken(String token);
}