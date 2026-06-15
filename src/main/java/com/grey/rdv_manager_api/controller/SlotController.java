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

import com.grey.rdv_manager_api.payload.request.CreateSlotRequest;
import com.grey.rdv_manager_api.payload.request.UpdateSlotRequest;
import com.grey.rdv_manager_api.payload.response.SlotResponse;
import com.grey.rdv_manager_api.service.SlotService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Slots", description = "Bookable time slots. Write operations are ADMIN only.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @Operation(summary = "Create a slot", description = "ADMIN only. End time must be after start time.")
    @ApiResponse(responseCode = "201", description = "Slot created")
    @PostMapping
    public ResponseEntity<SlotResponse> createSlot(
            @Validated @RequestBody CreateSlotRequest request) {
        SlotResponse response = slotService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get slot by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SlotResponse> getSlot(@PathVariable UUID id) {
        SlotResponse response = slotService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "List all slots",
        description = "Client portal filters this list client-side by serviceId and available > 0."
    )
    @GetMapping
    public ResponseEntity<List<SlotResponse>> getAllSlots() {
        List<SlotResponse> list = slotService.getAll();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Update a slot", description = "ADMIN only")
    @PutMapping("/{id}")
    public ResponseEntity<SlotResponse> updateSlot(
            @PathVariable UUID id,
            @Validated @RequestBody UpdateSlotRequest request) {
        SlotResponse response = slotService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a slot", description = "ADMIN only")
    @ApiResponse(responseCode = "204", description = "Deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSlot(@PathVariable UUID id) {
        slotService.delete(id);
    }
}