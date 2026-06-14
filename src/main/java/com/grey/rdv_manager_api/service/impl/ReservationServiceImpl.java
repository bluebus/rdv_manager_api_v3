package com.grey.rdv_manager_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grey.rdv_manager_api.domain.model.Reservation;
import com.grey.rdv_manager_api.domain.model.Slot;
import com.grey.rdv_manager_api.mapper.ReservationMapper;
import com.grey.rdv_manager_api.payload.request.CreateReservationRequest;
import com.grey.rdv_manager_api.payload.request.UpdateReservationRequest;
import com.grey.rdv_manager_api.payload.response.ReservationResponse;
import com.grey.rdv_manager_api.repository.ReservationRepository;
import com.grey.rdv_manager_api.repository.SlotRepository;
import com.grey.rdv_manager_api.service.AuditLogService;
import com.grey.rdv_manager_api.service.ReservationService;
import com.grey.rdv_manager_api.domain.enums.ReservationStatus;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository repository;
    private final ReservationMapper mapper;
    private final SlotRepository slotRepository;

    //202606 update log
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public ReservationResponse create(CreateReservationRequest request) {

        // 202606 Validate slot exists
        Slot slot = slotRepository.findById(request.slotId())
            .orElseThrow(() -> new RuntimeException("Slot not found: " + request.slotId()));

        // Check capacity — counts only CONFIRMED reservations for this slot
        long confirmedCount = repository.findBySlotId(request.slotId())
            .stream()
            .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
            .count();

        if (confirmedCount >= slot.getCapacity()) {
            throw new RuntimeException("No available spots for this slot.");
        }
        //end new part

        Reservation entity = mapper.toEntity(request);
        entity.setId(UUID.randomUUID());

        //202606 set initial status for Reservation.status
        entity.setStatus(ReservationStatus.PENDING);  

        entity.setCreatedAt(LocalDateTime.now());  // ← add this as fallback
        entity.setUpdatedAt(LocalDateTime.now());  // ← add this as fallback

        Reservation saved = repository.save(entity);

        //202606 update log 
        auditLogService.log(
            "Reservation", saved.getId(),
            "CREATE",
            String.valueOf(request.clientId()),
            "Client " + request.clientId() + " booked slot " + request.slotId()
        );  

        return mapper.toResponse(saved);
    }

    @Override
    public ReservationResponse getById(UUID id) {
        Reservation entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + id));
        return mapper.toResponse(entity);
    }

    @Override
    public List<ReservationResponse> getAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    @Transactional
    public ReservationResponse update(UUID id, UpdateReservationRequest request) {
        /*Reservation entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + id));
        mapper.updateEntity(request, entity);
        Reservation updated = repository.save(entity);
        return mapper.toResponse(updated);*/

        //202606 update catcher
        Reservation entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + id));

        ReservationStatus oldStatus = entity.getStatus();
        mapper.updateEntity(request, entity);
        ReservationStatus newStatus = entity.getStatus();

        Slot slot = slotRepository.findById(entity.getSlotId())
            .orElseThrow(() -> new RuntimeException("Slot not found: " + entity.getSlotId()));

        // PENDING → CONFIRMED: decrement available
        if (oldStatus == ReservationStatus.PENDING
                && newStatus == ReservationStatus.CONFIRMED) {

            if (slot.getAvailable() <= 0) {
                throw new RuntimeException(
                    "Cannot confirm — slot has no remaining capacity.");
            }
            slot.setAvailable(slot.getAvailable() - 1);
            slotRepository.save(slot);
        }

        // CONFIRMED → CANCELLED: restore available
        if (oldStatus == ReservationStatus.CONFIRMED
                && newStatus == ReservationStatus.CANCELLED) {

            slot.setAvailable(Math.min(slot.getAvailable() + 1, slot.getCapacity()));
            slotRepository.save(slot);
        }

        // PENDING → CANCELLED: no slot change needed
        Reservation updated = repository.save(entity);

        //202606 update log
        auditLogService.log(
            "Reservation", updated.getId(),
            "UPDATE",
            "ADMIN",
            "Status changed from " + oldStatus + " to " + newStatus
        );

        return mapper.toResponse(updated);
        //end new part
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        // 202606 update to verify exists before delete
        Reservation entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + id));

        // if reservation was CONFIRMED, restore slot availability
        if (entity.getStatus() == ReservationStatus.CONFIRMED) {
            Slot slot = slotRepository.findById(entity.getSlotId()).orElse(null);
            if (slot != null) {
                slot.setAvailable(Math.min(slot.getAvailable() + 1, slot.getCapacity()));
                slotRepository.save(slot);
            }
        }

        //202606 update log
        auditLogService.log(
            "Reservation", entity.getId(),
            "DELETE",
            "ADMIN",
            "Reservation deleted — was " + entity.getStatus()
        );

        repository.deleteById(id);
    }

    //202606 new part to extract the booking by using client ID
    @Override
    public List<ReservationResponse> getByClientId(UUID clientId) {
        return mapper.toResponseList(repository.findByClientId(clientId));
    }
    //end new part
}