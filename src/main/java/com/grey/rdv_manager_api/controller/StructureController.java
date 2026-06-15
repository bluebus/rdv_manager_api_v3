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

import com.grey.rdv_manager_api.payload.request.CreateStructureRequest;
import com.grey.rdv_manager_api.payload.request.UpdateStructureRequest;
import com.grey.rdv_manager_api.payload.response.StructureResponse;
import com.grey.rdv_manager_api.service.StructureService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Structures", description = "Manage physical locations — ADMIN only")
@SecurityRequirement(name = "bearerAuth")

@RestController
@RequestMapping("/api/structures")
@RequiredArgsConstructor
public class StructureController {

    private final StructureService structureService;
    
    @Operation(summary = "Create a structure")
    @ApiResponse(responseCode = "201", description = "Structure created")
    @PostMapping
    public ResponseEntity<StructureResponse> createStructure(
            @Validated @RequestBody CreateStructureRequest request) {
        StructureResponse response = structureService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get structure by ID")
    @GetMapping("/{id}")
    public ResponseEntity<StructureResponse> getStructure(@PathVariable UUID id) {
        StructureResponse response = structureService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all structures")
    @GetMapping
    public ResponseEntity<List<StructureResponse>> getAllStructures() {
        List<StructureResponse> list = structureService.getAll();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Update a structure")
    @PutMapping("/{id}")
    public ResponseEntity<StructureResponse> updateStructure(
            @PathVariable UUID id,
            @Validated @RequestBody UpdateStructureRequest request) {
        StructureResponse response = structureService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a structure")
    @ApiResponse(responseCode = "204", description = "Deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStructure(@PathVariable UUID id) {
        structureService.delete(id);
    }
}
