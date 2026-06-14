package com.grey.rdv_manager_api.mapper;

import org.mapstruct.*;

import com.grey.rdv_manager_api.domain.enums.Role;
import com.grey.rdv_manager_api.domain.model.Client;
import com.grey.rdv_manager_api.payload.request.CreateClientRequest;
import com.grey.rdv_manager_api.payload.request.UpdateClientRequest;
import com.grey.rdv_manager_api.payload.response.ClientResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    
    //202606 explicit roles mapping
    @Mapping(target = "roles",        source = "roles", qualifiedByName = "stringListToRoles")
    //end new part
    
    Client toEntity(CreateClientRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    //202606 explicit roles mapping
    @Mapping(target = "roles",        source = "roles", qualifiedByName = "stringSetToRoles")
    //end new part

    Client updateEntity(UpdateClientRequest dto, @MappingTarget Client entity);

    ClientResponse toResponse(Client entity);

    List<ClientResponse> toResponseList(List<Client> entities);

    // ── Named helper: List<String> → List<Role> ───────────────────────────────
    // Used by toEntity() via @Named qualifier
    // Accepts List<String> from CreateClientRequest.roles
    // Returns List<Role> for Client.roles
    // .toUpperCase() is safe here — r is always String from the DTO
    @Named("stringListToRoles")
    default List<Role> stringListToRoleList(List<String> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(r -> Role.valueOf(r.toUpperCase()))
                .collect(Collectors.toList());
    }

    // ── Named helper: Set<String> → List<Role> ────────────────────────────────
    // Used by updateEntity() via @Named qualifier
    // Accepts Set<String> from UpdateClientRequest.roles
    // Returns List<Role> for Client.roles
    @Named("stringSetToRoles")
    default List<Role> stringSetToRoleList(Set<String> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(r -> Role.valueOf(r.toUpperCase()))
                .collect(Collectors.toList());
    }
}