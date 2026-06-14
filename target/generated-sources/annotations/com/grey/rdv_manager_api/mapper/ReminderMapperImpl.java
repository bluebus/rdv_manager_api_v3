package com.grey.rdv_manager_api.mapper;

import com.grey.rdv_manager_api.domain.enums.ReminderMethod;
import com.grey.rdv_manager_api.domain.model.Reminder;
import com.grey.rdv_manager_api.payload.request.CreateReminderRequest;
import com.grey.rdv_manager_api.payload.request.UpdateReminderRequest;
import com.grey.rdv_manager_api.payload.response.ReminderResponse;
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
public class ReminderMapperImpl implements ReminderMapper {

    @Override
    public Reminder toEntity(CreateReminderRequest dto) {
        if ( dto == null ) {
            return null;
        }

        Reminder.ReminderBuilder reminder = Reminder.builder();

        reminder.reservationId( dto.reservationId() );
        reminder.reminderTime( dto.reminderTime() );
        if ( dto.method() != null ) {
            reminder.method( Enum.valueOf( ReminderMethod.class, dto.method() ) );
        }

        return reminder.build();
    }

    @Override
    public Reminder updateEntity(UpdateReminderRequest dto, Reminder entity) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.reminderTime() != null ) {
            entity.setReminderTime( dto.reminderTime() );
        }
        if ( dto.method() != null ) {
            entity.setMethod( dto.method() );
        }
        if ( dto.sent() != null ) {
            entity.setSent( dto.sent() );
        }

        return entity;
    }

    @Override
    public ReminderResponse toResponse(Reminder entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        UUID reservationId = null;
        LocalDateTime reminderTime = null;
        String method = null;
        boolean sent = false;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = entity.getId();
        reservationId = entity.getReservationId();
        reminderTime = entity.getReminderTime();
        if ( entity.getMethod() != null ) {
            method = entity.getMethod().name();
        }
        sent = entity.isSent();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();

        ReminderResponse reminderResponse = new ReminderResponse( id, reservationId, reminderTime, method, sent, createdAt, updatedAt );

        return reminderResponse;
    }

    @Override
    public List<ReminderResponse> toResponseList(List<Reminder> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ReminderResponse> list = new ArrayList<ReminderResponse>( entities.size() );
        for ( Reminder reminder : entities ) {
            list.add( toResponse( reminder ) );
        }

        return list;
    }
}
