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

import com.grey.rdv_manager_api.payload.request.CreateReminderRequest;
import com.grey.rdv_manager_api.payload.request.UpdateReminderRequest;
import com.grey.rdv_manager_api.payload.response.ReminderResponse;
import com.grey.rdv_manager_api.service.ReminderService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Reminders", description = "Notification reminders for reservations")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @Operation(summary = "Create a reminder", description = "Reminder time must be in the future.")
    @ApiResponse(responseCode = "201", description = "Reminder created")
    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(
            @Validated @RequestBody CreateReminderRequest request) {
        ReminderResponse response = reminderService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get reminder by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ReminderResponse> getReminder(@PathVariable UUID id) {
        ReminderResponse response = reminderService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all reminders")
    @GetMapping
    public ResponseEntity<List<ReminderResponse>> getAllReminders() {
        List<ReminderResponse> list = reminderService.getAll();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Update a reminder")
    @PutMapping("/{id}")
    public ResponseEntity<ReminderResponse> updateReminder(
            @PathVariable UUID id,
            @Validated @RequestBody UpdateReminderRequest request) {
        ReminderResponse response = reminderService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a reminder")
    @ApiResponse(responseCode = "204", description = "Deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReminder(@PathVariable UUID id) {
        reminderService.delete(id);
    }
}