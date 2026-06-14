package com.grey.rdv_manager_api.mapper;

import org.mapstruct.*;

import com.grey.rdv_manager_api.domain.model.Client;
import com.grey.rdv_manager_api.payload.request.CreateClientRequest;
import com.grey.rdv_manager_api.payload.request.UpdateClientRequest;
import com.grey.rdv_manager_api.payload.response.ClientResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    
    //202606 explicit roles mapping
    @Mapping(
    target = "roles",
    expression = "java(dto.roles().stream()" +
        ".map(r -> com.grey.rdv_manager_api.domain.enums.Role.valueOf(r.toUpperCase()))" +
        ".collect(java.util.stream.Collectors.toList()))"
    )
    //end new part
    
    Client toEntity(CreateClientRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Client updateEntity(UpdateClientRequest dto, @MappingTarget Client entity);

    ClientResponse toResponse(Client entity);

    List<ClientResponse> toResponseList(List<Client> entities);
}