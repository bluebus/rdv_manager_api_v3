package com.grey.rdv_manager_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grey.rdv_manager_api.payload.response.AuditLogResponse;
import com.grey.rdv_manager_api.service.AuditLogService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Audit Logs", description = "Read-only system log — ADMIN only")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Operation(summary = "Get audit log by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AuditLogResponse> getAuditLog(@PathVariable UUID id) {
        AuditLogResponse response = auditLogService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all audit logs")
    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {
        List<AuditLogResponse> list = auditLogService.getAll();
        return ResponseEntity.ok(list);
    }

    @Operation(
        summary = "Filter audit logs by entity",
        description = "Pass entityName (e.g. Reservation) and entityId (UUID) as query params."
    )
    @GetMapping("/entity")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEntity(
            @RequestParam String entityName,
            @RequestParam UUID entityId) {
        List<AuditLogResponse> list = auditLogService.getByEntity(entityName, entityId);
        return ResponseEntity.ok(list);
    }
}