package com.grey.rdv_manager_api.mapper;

import com.grey.rdv_manager_api.domain.model.ServiceEntity;
import com.grey.rdv_manager_api.payload.request.CreateServiceRequest;
import com.grey.rdv_manager_api.payload.request.UpdateServiceRequest;
import com.grey.rdv_manager_api.payload.response.ServiceResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-14T19:37:44+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
)
@Component
public class ServiceEntityMapperImpl implements ServiceEntityMapper {

    @Override
    public ServiceEntity toEntity(CreateServiceRequest dto) {
        if ( dto == null ) {
            return null;
        }

        ServiceEntity.ServiceEntityBuilder serviceEntity = ServiceEntity.builder();

        serviceEntity.structureId( dto.structureId() );
        serviceEntity.name( dto.name() );
        serviceEntity.description( dto.description() );
        serviceEntity.timezone( dto.timezone() );

        return serviceEntity.build();
    }

    @Override
    public ServiceEntity updateEntity(UpdateServiceRequest dto, ServiceEntity entity) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.name() != null ) {
            entity.setName( dto.name() );
        }
        if ( dto.description() != null ) {
            entity.setDescription( dto.description() );
        }
        if ( dto.timezone() != null ) {
            entity.setTimezone( dto.timezone() );
        }

        return entity;
    }

    @Override
    public ServiceResponse toResponse(ServiceEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        UUID structureId = null;
        String name = null;
        String description = null;
        String timezone = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = entity.getId();
        structureId = entity.getStructureId();
        name = entity.getName();
        description = entity.getDescription();
        timezone = entity.getTimezone();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();

        ServiceResponse serviceResponse = new ServiceResponse( id, structureId, name, description, timezone, createdAt, updatedAt );

        return serviceResponse;
    }

    @Override
    public List<ServiceResponse> toResponseList(List<ServiceEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ServiceResponse> list = new ArrayList<ServiceResponse>( entities.size() );
        for ( ServiceEntity serviceEntity : entities ) {
            list.add( toResponse( serviceEntity ) );
        }

        return list;
    }
}
