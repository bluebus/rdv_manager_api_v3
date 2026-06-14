package com.grey.rdv_manager_api.mapper;

import com.grey.rdv_manager_api.domain.enums.Role;
import com.grey.rdv_manager_api.domain.model.Client;
import com.grey.rdv_manager_api.payload.request.CreateClientRequest;
import com.grey.rdv_manager_api.payload.request.UpdateClientRequest;
import com.grey.rdv_manager_api.payload.response.ClientResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-14T18:13:40+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
)
@Component
public class ClientMapperImpl implements ClientMapper {

    @Override
    public Client toEntity(CreateClientRequest dto) {
        if ( dto == null ) {
            return null;
        }

        Client.ClientBuilder client = Client.builder();

        client.firstName( dto.firstName() );
        client.lastName( dto.lastName() );
        client.email( dto.email() );
        client.phone( dto.phone() );
        client.structureId( dto.structureId() );

        client.roles( dto.roles().stream().map(r -> com.grey.rdv_manager_api.domain.enums.Role.valueOf(r.toUpperCase())).collect(java.util.stream.Collectors.toList()) );

        return client.build();
    }

    @Override
    public Client updateEntity(UpdateClientRequest dto, Client entity) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.firstName() != null ) {
            entity.setFirstName( dto.firstName() );
        }
        if ( dto.lastName() != null ) {
            entity.setLastName( dto.lastName() );
        }
        if ( dto.email() != null ) {
            entity.setEmail( dto.email() );
        }
        if ( dto.phone() != null ) {
            entity.setPhone( dto.phone() );
        }
        if ( entity.getRoles() != null ) {
            List<Role> list = stringSetToRoleList( dto.roles() );
            if ( list != null ) {
                entity.getRoles().clear();
                entity.getRoles().addAll( list );
            }
        }
        else {
            List<Role> list = stringSetToRoleList( dto.roles() );
            if ( list != null ) {
                entity.setRoles( list );
            }
        }

        return entity;
    }

    @Override
    public ClientResponse toResponse(Client entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        String phone = null;
        List<Role> roles = null;
        UUID structureId = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        email = entity.getEmail();
        phone = entity.getPhone();
        List<Role> list = entity.getRoles();
        if ( list != null ) {
            roles = new ArrayList<Role>( list );
        }
        structureId = entity.getStructureId();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();

        ClientResponse clientResponse = new ClientResponse( id, firstName, lastName, email, phone, roles, structureId, createdAt, updatedAt );

        return clientResponse;
    }

    @Override
    public List<ClientResponse> toResponseList(List<Client> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ClientResponse> list = new ArrayList<ClientResponse>( entities.size() );
        for ( Client client : entities ) {
            list.add( toResponse( client ) );
        }

        return list;
    }

    protected List<Role> stringSetToRoleList(Set<String> set) {
        if ( set == null ) {
            return null;
        }

        List<Role> list = new ArrayList<Role>( set.size() );
        for ( String string : set ) {
            list.add( Enum.valueOf( Role.class, string ) );
        }

        return list;
    }
}
