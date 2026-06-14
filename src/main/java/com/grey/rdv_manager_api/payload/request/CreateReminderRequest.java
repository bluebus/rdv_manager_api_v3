package com.grey.rdv_manager_api.payload.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateReminderRequest(
/*@NotNull UUID reservationId,
@NotNull LocalDateTime reminderTime,
@NotNull String method*/

    //202606  update the data verification field
    @NotNull(message = "Reservation ID is required")
    UUID reservationId,

    @NotNull(message = "Reminder time is required")
    @Future(message = "Reminder time must be in the future")
    LocalDateTime reminderTime,

    // Must be one of: EMAIL, SMS, PUSH
    @NotBlank(message = "Method is required")
    @Pattern(
        regexp = "^(EMAIL|SMS|PUSH)$",
        message = "Method must be one of: EMAIL, SMS, PUSH"
    )
    String method
        ) {}
