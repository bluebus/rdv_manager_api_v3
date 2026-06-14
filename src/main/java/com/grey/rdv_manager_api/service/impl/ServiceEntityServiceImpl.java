package com.grey.rdv_manager_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grey.rdv_manager_api.domain.model.ServiceEntity;
import com.grey.rdv_manager_api.domain.model.Structure;
import com.grey.rdv_manager_api.mapper.ServiceEntityMapper;
import com.grey.rdv_manager_api.payload.request.CreateServiceRequest;
import com.grey.rdv_manager_api.payload.request.UpdateServiceRequest;
import com.grey.rdv_manager_api.payload.response.ServiceResponse;
import com.grey.rdv_manager_api.repository.ServiceRepository;
import com.grey.rdv_manager_api.repository.StructureRepository;
import com.grey.rdv_manager_api.service.ServiceEntityService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceEntityServiceImpl implements ServiceEntityService {

    private final ServiceRepository repository;
    private final ServiceEntityMapper mapper;

    //202606 add to initialize structure
    private final StructureRepository structureRepository;

    @Override
    @Transactional
    public ServiceResponse create(CreateServiceRequest request) {
        ServiceEntity entity = mapper.toEntity(request);
        entity.setId(UUID.randomUUID());
        entity.setTimezone("Asia/Kuala_Lumpur"); // default — not exposed to frontend
        ServiceEntity saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public ServiceResponse getById(UUID id) {
        ServiceEntity entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service not found: " + id));
        return mapper.toResponse(entity);
    }

    @Override
    public List<ServiceResponse> getAll() {
        return repository.findAll().stream()
            .map(this::toEnrichedResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceResponse update(UUID id, UpdateServiceRequest request) {
        ServiceEntity entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service not found: " + id));
        mapper.updateEntity(request, entity);
        ServiceEntity updated = repository.save(entity);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    // 202606 enrich response with structure name
    private ServiceResponse toEnrichedResponse(ServiceEntity entity) {
        ServiceResponse base = mapper.toResponse(entity);
        String structureName = null;
        if (entity.getStructureId() != null) {
            structureName = structureRepository.findById(entity.getStructureId())
                .map(Structure::getName)
                .orElse(null);
        }
        return new ServiceResponse(
            base.id(),
            base.structureId(),
            structureName,        // ← injected here
            base.name(),
            base.description(),
            base.timezone(),
            base.createdAt(),
            base.updatedAt()
        );
    }
}