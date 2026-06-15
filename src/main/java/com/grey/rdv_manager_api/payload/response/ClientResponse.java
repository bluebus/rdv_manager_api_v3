package com.grey.rdv_manager_api.payload.response;

import com.grey.rdv_manager_api.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Client account details — passwordHash is never returned")
public record ClientResponse(
        UUID id,

        @Schema(example = "Jane")
        String firstName,

        @Schema(example = "Doe")
        String lastName,

        @Schema(example = "jane@example.com")
        String email,

        @Schema(example = "+60123456789")
        String phone,


        //List<String> roles  
        //202606 update roles atrribute
        @Schema(description = "One or more roles", example = "[\"CLIENT\"]")
        List<Role> roles,
        
        UUID structureId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {}
