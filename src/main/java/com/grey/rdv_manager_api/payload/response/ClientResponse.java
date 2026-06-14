package com.grey.rdv_manager_api.payload.response;

import com.grey.rdv_manager_api.domain.enums.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,

        //List<String> roles  
        //202606 update roles atrribute
        List<Role> roles,
        
        UUID structureId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {}
