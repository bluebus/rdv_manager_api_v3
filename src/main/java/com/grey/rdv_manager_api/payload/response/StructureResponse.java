package com.grey.rdv_manager_api.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Physical location that offers services")
public record StructureResponse(

    UUID id,

    @Schema(example = "Klinik Cahaya")
    String name,

    String description,

    @Schema(example = "123 Jalan Ampang, Kuala Lumpur")
    String address,

    @Schema(example = "+60312345678")
    String phone,

    @Schema(example = "info@klinikcahaya.com")
    String email,

    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}