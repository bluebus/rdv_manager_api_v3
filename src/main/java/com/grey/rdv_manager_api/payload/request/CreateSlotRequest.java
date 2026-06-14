package com.grey.rdv_manager_api.payload.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateSlotRequest(
/*@NotNull UUID serviceId,
@NotNull LocalDate date,
@NotNull LocalTime startTime,
@NotNull LocalTime endTime,
@Min(1) int capacity*/

        // 20260606 update the data verification field
        @NotNull(message = "Service ID is required")
        UUID serviceId,

        @NotNull(message = "Date is required")
        @FutureOrPresent(message = "Date must be today or in the future")
        LocalDate date,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        @Min(value = 1, message = "Capacity must be at least 1")
        @Max(value = 500, message = "Capacity must not exceed 500")
        int capacity

        ){
                //
                public CreateSlotRequest {
                        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
                        throw new jakarta.validation.ValidationException(
                                "End time must be later than start time"
                        );
                        }
                }


        }
