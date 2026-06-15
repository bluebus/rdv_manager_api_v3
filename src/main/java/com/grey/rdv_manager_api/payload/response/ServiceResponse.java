package com.grey.rdv_manager_api.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Service offered by a structure")
public record ServiceResponse(

    UUID id,

    @Schema(description = "Owning structure UUID")
    UUID structureId,

    @Schema(description = "Structure name — embedded to avoid extra client-side lookups",
            example = "Klinik Cahaya")
    String structureName,

    @Schema(example = "General Consultation")
    String name,

    String description,

    @Schema(description = "Timezone — defaults to Asia/Kuala_Lumpur",
            example = "Asia/Kuala_Lumpur")
    String timezone,

    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}