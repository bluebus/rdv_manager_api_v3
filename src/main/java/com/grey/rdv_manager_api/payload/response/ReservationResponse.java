package com.grey.rdv_manager_api.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Reservation details")
public record ReservationResponse(

    @Schema(description = "Reservation UUID")
    UUID id,

    @Schema(description = "Client who made the booking")
    UUID clientId,

    @Schema(description = "Slot that was booked")
    UUID slotId,

    @Schema(description = "Current status", example = "PENDING",
            allowableValues = {"PENDING", "CONFIRMED", "CANCELLED"})
    String status,

    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}