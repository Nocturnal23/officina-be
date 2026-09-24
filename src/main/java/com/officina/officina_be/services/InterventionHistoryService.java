package com.officina.officina_be.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officina.officina_be.core.utils.Constants.BookingStatus;
import com.officina.officina_be.dto.request.InterventionHistoryRequest;
import com.officina.officina_be.dto.response.BookingResponse;
import com.officina.officina_be.dto.response.InterventionHistoryResponse;
import com.officina.officina_be.dto.response.PageResponse;
import com.officina.officina_be.entity.Booking;
import com.officina.officina_be.entity.InterventionHistory;
import com.officina.officina_be.entity.Jobs;
import com.officina.officina_be.entity.Vehicle;
import com.officina.officina_be.error.ResourceNotFound;
import com.officina.officina_be.mapper.InterventionHistoryMapper;
import com.officina.officina_be.repository.InterventionHistoryRepository;
import com.officina.officina_be.repository.JobsRepository;
import com.officina.officina_be.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterventionHistoryService {
    private final VehicleRepository vehicleRepo;
    private final JobsRepository jobsRepo;
    private final InterventionHistoryRepository interventionHistoryRepo;

    private final InterventionHistoryMapper interventionHistoryMapper;

    @Transactional
    public InterventionHistoryResponse saveIntervention(InterventionHistoryRequest request, Long vehicleId) {
        Vehicle vehicle = vehicleRepo.findByIdAndDeletedFalse(vehicleId)
                .orElseThrow(() -> new ResourceNotFound("Veicolo non trovato."));

        Set<Jobs> jobs = new HashSet<>(
                jobsRepo.findByIdInAndDeletedFalse(request.jobsIds()));

        InterventionHistory interventionHistory = interventionHistoryMapper.toEntity(request);
        interventionHistory.setVehicle(vehicle);
        interventionHistory.setJobs(jobs);

        InterventionHistory savedIntervention = interventionHistoryRepo.save(interventionHistory);

        return interventionHistoryMapper.toResponse(savedIntervention);
    }

    @Transactional
    public PageResponse<InterventionHistoryResponse> getInterventionsByVehicleId(Long vehicleId, int page, int size, List<BookingStatus> statuses) {
        vehicleRepo.findByIdAndDeletedFalse(vehicleId)
                .orElseThrow(() -> new ResourceNotFound("Veicolo non trovato."));

        Pageable pageable = PageRequest.of(page, size, Sort.by("completedAt").ascending());

        Page<InterventionHistory> interventionPage;

        if (statuses == null || statuses.isEmpty()) {
            interventionPage = interventionHistoryRepo.findByVehicle_IdAndDeletedFalse(vehicleId, pageable);
        } else {
            interventionPage = interventionHistoryRepo.findByVehicle_IdAndStatusInAndDeletedFalse(vehicleId, statuses, pageable);
        }

        return interventionHistoryMapper.toPageResponse(interventionPage);
    }

    @Transactional 
    public void deleteIntervention(Long id) {
        InterventionHistory intervention = interventionHistoryRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFound("Intervento non trovato."));

        intervention.setDeleted(true);
        interventionHistoryRepo.save(intervention);
    }

    @Transactional 
    public InterventionHistoryResponse updateIntervention(Long id, InterventionHistoryRequest request) {
        InterventionHistory intervention = interventionHistoryRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFound("Intervento non trovato."));

        intervention.setKilometers(request.kilometers());
        intervention.setCost(request.cost());
        intervention.setNotes(request.notes());

        if(request.jobsIds() != null) {
            Set<Jobs> jobs = new HashSet<>(jobsRepo.findByIdInAndDeletedFalse(request.jobsIds()));
            intervention.setJobs(jobs);
        }

        InterventionHistory updated = interventionHistoryRepo.save(intervention);
        return interventionHistoryMapper.toResponse(updated);
    }

    @Transactional
    public PageResponse<InterventionHistoryResponse> getInterventionsHistoryByVehicleId(Long vehicleId, int page, int size,
            List<BookingStatus> filterStatuses) {
        vehicleRepo.findByIdAndDeletedFalse(vehicleId)
                .orElseThrow(() -> new ResourceNotFound("Veicolo non trovato."));

        if (filterStatuses == null || filterStatuses.isEmpty()) {
            filterStatuses = List.of(BookingStatus.COMPLETED, BookingStatus.REFUSED);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending());
        Page<InterventionHistory> interventionPage = interventionHistoryRepo.findByVehicle_IdAndStatusInAndDeletedFalse(vehicleId, filterStatuses, pageable);

        return interventionHistoryMapper.toPageResponse(interventionPage);
    }
}
