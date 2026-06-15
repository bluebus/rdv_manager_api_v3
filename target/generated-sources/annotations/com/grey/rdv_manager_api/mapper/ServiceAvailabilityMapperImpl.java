package com.grey.rdv_manager_api.mapper;

import com.grey.rdv_manager_api.domain.enums.Weekday;
import com.grey.rdv_manager_api.domain.model.ServiceAvailability;
import com.grey.rdv_manager_api.payload.request.CreateServiceAvailabilityRequest;
import com.grey.rdv_manager_api.payload.request.UpdateServiceAvailabilityRequest;
import com.grey.rdv_manager_api.payload.response.ServiceAvailabilityResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-15T13:53:13+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
)
@Component
public class ServiceAvailabilityMapperImpl implements ServiceAvailabilityMapper {

    @Override
    public ServiceAvailability toEntity(CreateServiceAvailabilityRequest dto) {
        if ( dto == null ) {
            return null;
        }

        ServiceAvailability.ServiceAvailabilityBuilder serviceAvailability = ServiceAvailability.builder();

        serviceAvailability.serviceId( dto.serviceId() );
        if ( dto.dayOfWeek() != null ) {
            serviceAvailability.dayOfWeek( Enum.valueOf( Weekday.class, dto.dayOfWeek() ) );
        }
        serviceAvailability.startTime( dto.startTime() );
        serviceAvailability.endTime( dto.endTime() );

        return serviceAvailability.build();
    }

    @Override
    public ServiceAvailability updateEntity(UpdateServiceAvailabilityRequest dto, ServiceAvailability entity) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.dayOfWeek() != null ) {
            entity.setDayOfWeek( dto.dayOfWeek() );
        }
        if ( dto.startTime() != null ) {
            entity.setStartTime( dto.startTime() );
        }
        if ( dto.endTime() != null ) {
            entity.setEndTime( dto.endTime() );
        }

        return entity;
    }

    @Override
    public ServiceAvailabilityResponse toResponse(ServiceAvailability entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        UUID serviceId = null;
        String dayOfWeek = null;
        LocalTime startTime = null;
        LocalTime endTime = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = entity.getId();
        serviceId = entity.getServiceId();
        if ( entity.getDayOfWeek() != null ) {
            dayOfWeek = entity.getDayOfWeek().name();
        }
        startTime = entity.getStartTime();
        endTime = entity.getEndTime();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();

        ServiceAvailabilityResponse serviceAvailabilityResponse = new ServiceAvailabilityResponse( id, serviceId, dayOfWeek, startTime, endTime, createdAt, updatedAt );

        return serviceAvailabilityResponse;
    }

    @Override
    public List<ServiceAvailabilityResponse> toResponseList(List<ServiceAvailability> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ServiceAvailabilityResponse> list = new ArrayList<ServiceAvailabilityResponse>( entities.size() );
        for ( ServiceAvailability serviceAvailability : entities ) {
            list.add( toResponse( serviceAvailability ) );
        }

        return list;
    }
}
