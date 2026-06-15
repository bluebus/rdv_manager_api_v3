package com.grey.rdv_manager_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * STEP 1 Represents a revoked JWT token stored in MongoDB.
 *
 * How it works:
 * - When a user logs out, their token is saved here with its expiry date.
 * - The @Indexed(expireAfter = "0s") on expiresAt tells MongoDB to automatically
 *   delete this document the moment expiresAt is reached.
 * - This means the blacklist is self-cleaning — no manual purging needed.
 * - The @Indexed(unique = true) on token prevents duplicate entries.
 *
 * Collection: token_blacklist
 *
 * IMPORTANT — why java.util.Date and NOT LocalDateTime:
 * MongoDB's TTL index only works with BSON Date type.
 * LocalDateTime serializes as an array [2026,6,15,10,30,0] in MongoDB,
 * which is NOT a BSON Date — the TTL index will silently ignore it.
 * Always use java.util.Date for TTL fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "token_blacklist")
public class TokenBlacklist {

    @Id
    private String id;

    /**
     * The full raw JWT string.
     * Unique index prevents duplicate blacklist entries on repeated logout calls.
     * Used by JwtAuthenticationFilter.existsByToken() on every authenticated request.
     */
    @Indexed(unique = true)
    private String token;

    /**
     * The exact moment this JWT expires — read from the token's own "exp" claim.
     * MongoDB's TTL mechanism deletes this document automatically at this timestamp.
     * expireAfter = "0s" means: delete exactly when expiresAt is reached, no extra delay.
     *
     * Must be java.util.Date — NOT LocalDateTime — for the TTL index to work.
     */
    @Indexed(expireAfter = "0s")
    private Date expiresAt;
}