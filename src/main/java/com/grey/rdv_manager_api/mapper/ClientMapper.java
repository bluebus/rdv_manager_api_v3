package com.grey.rdv_manager_api.mapper;

import org.mapstruct.*;

import com.grey.rdv_manager_api.domain.enums.Role;
import com.grey.rdv_manager_api.domain.model.Client;
import com.grey.rdv_manager_api.payload.request.CreateClientRequest;
import com.grey.rdv_manager_api.payload.request.UpdateClientRequest;
import com.grey.rdv_manager_api.payload.response.ClientResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    
    //202606 explicit roles mapping
    @Mapping(target = "roles",        source = "roles", qualifiedByName = "listRolesToList")
    //end new part
    
    Client toEntity(CreateClientRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    //202606 explicit roles mapping
    @Mapping(target = "roles", source = "roles", qualifiedByName = "setRolesToList")
    //end new part

    Client updateEntity(UpdateClientRequest dto, @MappingTarget Client entity);

    ClientResponse toResponse(Client entity);

    List<ClientResponse> toResponseList(List<Client> entities);

    //202606 add new defination
    // ── toEntity() helper: List<Role> → List<Role> ───────────────────────────
    // CreateClientRequest.roles is already List<Role> (correct enum),
    // so this just passes through — but the explicit qualifier prevents
    // MapStruct from attempting any ambiguous auto-mapping.
    @Named("listRolesToList")
    default List<Role> listRolesToList(List<Role> roles) {
        if (roles == null) return null;
        return new ArrayList<>(roles);
    }

    // ── updateEntity() helper: Set<Role> → List<Role> ────────────────────────
    // UpdateClientRequest.roles is Set<Role> (correct enum, fixed import),
    // Client.roles is List<Role> — MapStruct needs explicit help for Set → List.
    @Named("setRolesToList")
    default List<Role> setRolesToList(Set<Role> roles) {
        if (roles == null) return null;
        return new ArrayList<>(roles);
    }
}