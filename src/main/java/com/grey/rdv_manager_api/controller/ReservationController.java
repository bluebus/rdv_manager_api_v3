package com.grey.rdv_manager_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.grey.rdv_manager_api.payload.request.CreateReservationRequest;
import com.grey.rdv_manager_api.payload.request.UpdateReservationRequest;
import com.grey.rdv_manager_api.payload.response.ReservationResponse;
import com.grey.rdv_manager_api.service.ReservationService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Reservations", description = "Client bookings of time slots")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
        summary = "Create a reservation",
        description = "Status defaults to PENDING. Fails if slot has no remaining capacity."
    )

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Validated @RequestBody CreateReservationRequest request) {
        ReservationResponse response = reservationService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get reservation by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable UUID id) {
        ReservationResponse response = reservationService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all reservations", description = "ADMIN use — returns all records")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> list = reservationService.getAll();
        return ResponseEntity.ok(list);
    }

    // ── 202606 filter by client — used by index.html My Bookings ──
    @Operation(
        summary = "Get reservations by client",
        description = "Used by the client portal My Bookings tab."
    )
    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<List<ReservationResponse>> getByClient(
            @PathVariable UUID clientId) {
        List<ReservationResponse> list = reservationService.getByClientId(clientId);
        return ResponseEntity.ok(list);
    }
    //end new part

    @Operation(
        summary = "Update reservation status",
        description = "PENDING → CONFIRMED decrements slot.available. CONFIRMED → CANCELLED restores it."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable UUID id,
            @Validated @RequestBody UpdateReservationRequest request) {
        ReservationResponse response = reservationService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete a reservation",
        description = "Restores slot.available if reservation was CONFIRMED."
    )
    @ApiResponse(responseCode = "204", description = "Deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable UUID id) {
        reservationService.delete(id);
    }
}