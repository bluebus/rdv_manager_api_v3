package com.grey.rdv_manager_api.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Single audit trail entry")
public record AuditLogResponse(

    UUID id,

    @Schema(description = "Name of the affected entity", example = "Reservation")
    String entityName,

    UUID entityId,

    @Schema(description = "Action performed", example = "CREATE",
            allowableValues = {"CREATE", "UPDATE", "DELETE"})
    String action,

    @Schema(description = "Who triggered the action", example = "admin@rdv.com")
    String performedBy,

    @Schema(description = "When the action occurred")
    LocalDateTime timestamp
) {}