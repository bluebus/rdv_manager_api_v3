package com.grey.rdv_manager_api.mapper;

import com.grey.rdv_manager_api.domain.model.Structure;
import com.grey.rdv_manager_api.payload.request.CreateStructureRequest;
import com.grey.rdv_manager_api.payload.request.UpdateStructureRequest;
import com.grey.rdv_manager_api.payload.response.StructureResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-14T22:17:21+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
)
@Component
public class StructureMapperImpl implements StructureMapper {

    @Override
    public Structure toEntity(CreateStructureRequest dto) {
        if ( dto == null ) {
            return null;
        }

        Structure.StructureBuilder structure = Structure.builder();

        structure.name( dto.name() );
        structure.description( dto.description() );
        structure.address( dto.address() );
        structure.phone( dto.phone() );
        structure.email( dto.email() );

        return structure.build();
    }

    @Override
    public Structure updateEntity(UpdateStructureRequest dto, Structure entity) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.name() != null ) {
            entity.setName( dto.name() );
        }
        if ( dto.description() != null ) {
            entity.setDescription( dto.description() );
        }
        if ( dto.address() != null ) {
            entity.setAddress( dto.address() );
        }
        if ( dto.phone() != null ) {
            entity.setPhone( dto.phone() );
        }
        if ( dto.email() != null ) {
            entity.setEmail( dto.email() );
        }

        return entity;
    }

    @Override
    public StructureResponse toResponse(Structure entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String description = null;
        String address = null;
        String phone = null;
        String email = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        address = entity.getAddress();
        phone = entity.getPhone();
        email = entity.getEmail();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();

        StructureResponse structureResponse = new StructureResponse( id, name, description, address, phone, email, createdAt, updatedAt );

        return structureResponse;
    }

    @Override
    public List<StructureResponse> toResponseList(List<Structure> entities) {
        if ( entities == null ) {
            return null;
        }

        List<StructureResponse> list = new ArrayList<StructureResponse>( entities.size() );
        for ( Structure structure : entities ) {
            list.add( toResponse( structure ) );
        }

        return list;
    }
}
