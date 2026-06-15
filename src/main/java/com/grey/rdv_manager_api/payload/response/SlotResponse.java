package com.grey.rdv_manager_api.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Schema(description = "Bookable time slot")
public record SlotResponse(

    UUID id,

    @Schema(description = "Linked service UUID")
    UUID serviceId,

    @Schema(description = "Date of the slot", example = "2026-07-01")
    LocalDate date,

    @Schema(description = "Start time", example = "09:00")
    LocalTime startTime,

    @Schema(description = "End time", example = "09:30")
    LocalTime endTime,

    @Schema(description = "Total capacity", example = "5")
    int capacity,

    @Schema(description = "Remaining spots — decremented on CONFIRM, restored on CANCEL",
            example = "3")
    int available,

    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}