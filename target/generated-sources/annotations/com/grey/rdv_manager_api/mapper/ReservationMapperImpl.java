package com.grey.rdv_manager_api.mapper;

import com.grey.rdv_manager_api.domain.model.Reservation;
import com.grey.rdv_manager_api.payload.request.CreateReservationRequest;
import com.grey.rdv_manager_api.payload.request.UpdateReservationRequest;
import com.grey.rdv_manager_api.payload.response.ReservationResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-14T16:15:38+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
)
@Component
public class ReservationMapperImpl implements ReservationMapper {

    @Override
    public Reservation toEntity(CreateReservationRequest dto) {
        if ( dto == null ) {
            return null;
        }

        Reservation.ReservationBuilder reservation = Reservation.builder();

        reservation.clientId( dto.clientId() );
        reservation.slotId( dto.slotId() );

        return reservation.build();
    }

    @Override
    public Reservation updateEntity(UpdateReservationRequest dto, Reservation entity) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.status() != null ) {
            entity.setStatus( dto.status() );
        }

        return entity;
    }

    @Override
    public ReservationResponse toResponse(Reservation entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        UUID clientId = null;
        UUID slotId = null;
        String status = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = entity.getId();
        clientId = entity.getClientId();
        slotId = entity.getSlotId();
        if ( entity.getStatus() != null ) {
            status = entity.getStatus().name();
        }
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();

        ReservationResponse reservationResponse = new ReservationResponse( id, clientId, slotId, status, createdAt, updatedAt );

        return reservationResponse;
    }

    @Override
    public List<ReservationResponse> toResponseList(List<Reservation> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ReservationResponse> list = new ArrayList<ReservationResponse>( entities.size() );
        for ( Reservation reservation : entities ) {
            list.add( toResponse( reservation ) );
        }

        return list;
    }
}
