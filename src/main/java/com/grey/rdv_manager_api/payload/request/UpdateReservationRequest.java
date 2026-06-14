package com.grey.rdv_manager_api.payload.request;

import jakarta.validation.constraints.NotNull;
import com.grey.rdv_manager_api.domain.enums.ReservationStatus;

public record UpdateReservationRequest(
    //ReservationStatus status

    //202606  update the data verification field
    @NotNull(message = "Status is required")
    ReservationStatus status
) {}
