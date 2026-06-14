package com.grey.rdv_manager_api.mapper;

import com.grey.rdv_manager_api.domain.model.Slot;
import com.grey.rdv_manager_api.payload.request.CreateSlotRequest;
import com.grey.rdv_manager_api.payload.request.UpdateSlotRequest;
import com.grey.rdv_manager_api.payload.response.SlotResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-13T19:58:07+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
)
@Component
public class SlotMapperImpl implements SlotMapper {

    @Override
    public Slot toEntity(CreateSlotRequest dto) {
        if ( dto == null ) {
            return null;
        }

        Slot.SlotBuilder slot = Slot.builder();

        slot.available( dto.capacity() );
        slot.date( dto.date() );
        slot.startTime( dto.startTime() );
        slot.endTime( dto.endTime() );
        slot.capacity( dto.capacity() );
        slot.serviceId( dto.serviceId() );

        return slot.build();
    }

    @Override
    public Slot updateEntity(UpdateSlotRequest dto, Slot entity) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.date() != null ) {
            entity.setDate( dto.date() );
        }
        if ( dto.startTime() != null ) {
            entity.setStartTime( dto.startTime() );
        }
        if ( dto.endTime() != null ) {
            entity.setEndTime( dto.endTime() );
        }
        if ( dto.capacity() != null ) {
            entity.setCapacity( dto.capacity() );
        }
        if ( dto.available() != null ) {
            entity.setAvailable( dto.available() );
        }

        return entity;
    }

    @Override
    public SlotResponse toResponse(Slot entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        UUID serviceId = null;
        LocalDate date = null;
        LocalTime startTime = null;
        LocalTime endTime = null;
        int capacity = 0;
        int available = 0;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = entity.getId();
        serviceId = entity.getServiceId();
        date = entity.getDate();
        startTime = entity.getStartTime();
        endTime = entity.getEndTime();
        capacity = entity.getCapacity();
        available = entity.getAvailable();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();

        SlotResponse slotResponse = new SlotResponse( id, serviceId, date, startTime, endTime, capacity, available, createdAt, updatedAt );

        return slotResponse;
    }

    @Override
    public List<SlotResponse> toResponseList(List<Slot> entities) {
        if ( entities == null ) {
            return null;
        }

        List<SlotResponse> list = new ArrayList<SlotResponse>( entities.size() );
        for ( Slot slot : entities ) {
            list.add( toResponse( slot ) );
        }

        return list;
    }
}
