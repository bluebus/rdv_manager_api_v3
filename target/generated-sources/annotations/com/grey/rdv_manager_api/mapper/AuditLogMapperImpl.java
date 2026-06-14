package com.grey.rdv_manager_api.mapper;

import com.grey.rdv_manager_api.domain.model.AuditLog;
import com.grey.rdv_manager_api.payload.response.AuditLogResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-15T00:19:59+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
)
@Component
public class AuditLogMapperImpl implements AuditLogMapper {

    @Override
    public AuditLogResponse toResponse(AuditLog entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        String entityName = null;
        UUID entityId = null;
        String action = null;
        String performedBy = null;
        LocalDateTime timestamp = null;

        id = entity.getId();
        entityName = entity.getEntityName();
        entityId = entity.getEntityId();
        action = entity.getAction();
        performedBy = entity.getPerformedBy();
        timestamp = entity.getTimestamp();

        AuditLogResponse auditLogResponse = new AuditLogResponse( id, entityName, entityId, action, performedBy, timestamp );

        return auditLogResponse;
    }

    @Override
    public List<AuditLogResponse> toResponseList(List<AuditLog> entities) {
        if ( entities == null ) {
            return null;
        }

        List<AuditLogResponse> list = new ArrayList<AuditLogResponse>( entities.size() );
        for ( AuditLog auditLog : entities ) {
            list.add( toResponse( auditLog ) );
        }

        return list;
    }
}
