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

import com.grey.rdv_manager_api.payload.request.CreateClientRequest;
import com.grey.rdv_manager_api.payload.request.UpdateClientRequest;
import com.grey.rdv_manager_api.payload.response.ClientResponse;
import com.grey.rdv_manager_api.service.ClientService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Clients", description = "Manage client accounts — ADMIN only")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Create a client", description = "ADMIN only. For self-registration use POST /api/auth/register instead.")
    @ApiResponse(responseCode = "201", description = "Client created")
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(
            @Validated @RequestBody CreateClientRequest request) {
        ClientResponse response = clientService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get client by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable UUID id) {
        ClientResponse response = clientService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all clients")
    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<ClientResponse> list = clientService.getAll();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Update a client")
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable UUID id,
            @Validated @RequestBody UpdateClientRequest request) {
        ClientResponse response = clientService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a client")
    @ApiResponse(responseCode = "204", description = "Deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable UUID id) {
        clientService.delete(id);
    }
}