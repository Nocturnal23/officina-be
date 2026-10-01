package com.officina.officina_be.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officina.officina_be.core.utils.Constants.BookingStatus;
import com.officina.officina_be.dto.request.InterventionHistoryRequest;
import com.officina.officina_be.dto.response.InterventionHistoryResponse;
import com.officina.officina_be.dto.response.PageResponse;
import com.officina.officina_be.entity.Booking;
import com.officina.officina_be.entity.InterventionHistory;
import com.officina.officina_be.entity.Jobs;
import com.officina.officina_be.entity.Vehicle;
import com.officina.officina_be.error.ResourceNotFound;
import com.officina.officina_be.mapper.InterventionHistoryMapper;
import com.officina.officina_be.repository.BookingRepository;
import com.officina.officina_be.repository.InterventionHistoryRepository;
import com.officina.officina_be.repository.JobsRepository;
import com.officina.officina_be.repository.UserRepository;
import com.officina.officina_be.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterventionHistoryService {
    private final VehicleRepository vehicleRepo;
    private final JobsRepository jobsRepo;
    private final InterventionHistoryRepository interventionHistoryRepo;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;

    private final InterventionHistoryMapper interventionHistoryMapper;

    @Transactional
    public InterventionHistoryResponse saveIntervention(InterventionHistoryRequest request, Long vehicleId) {
        Vehicle vehicle = vehicleRepo.findByIdAndDeletedFalse(vehicleId)
                .orElseThrow(() -> new ResourceNotFound("Veicolo non trovato."));

        Long lastKm = getLastVehicleKilometers(vehicleId);
        if (request.kilometers() != null && request.kilometers() < lastKm) {
            throw new IllegalArgumentException("I chilometri inseriti (" + request.kilometers() + ") sono inferiori a quelli dell'ultimo intervento (" + lastKm + ").");
        }

        Set<Jobs> jobs = new HashSet<>(
                jobsRepo.findByIdInAndDeletedFalse(request.jobsIds()));

        InterventionHistory interventionHistory = interventionHistoryMapper.toEntity(request);
        interventionHistory.setVehicle(vehicle);
        interventionHistory.setJobs(jobs);

        if (request.bookingId() != null) {
            Booking booking = bookingRepo.findById(request.bookingId())
                    .orElseThrow(() -> new ResourceNotFound("Prenotazione non trovata."));
            interventionHistory.setBooking(booking);
        }

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
    public PageResponse<InterventionHistoryResponse> getInterventionsHistoryByUserId(Long userId, int page, int size, List<BookingStatus> filterStatuses) {
        userRepo.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFound("Utente non trovato."));

        if (filterStatuses == null || filterStatuses.isEmpty()) {
            filterStatuses = List.of(BookingStatus.COMPLETED, BookingStatus.REFUSED);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("completedAt").descending());
        Page<InterventionHistory> interventionPage = interventionHistoryRepo.findByVehicleUserIdAndStatusIn(userId, filterStatuses, pageable);

        return interventionHistoryMapper.toPageResponse(interventionPage);
    }

    @Transactional 
    public Long getLastVehicleKilometers(Long vehicleId) {
        Optional<InterventionHistory> lastIntervention = interventionHistoryRepo.findFirstByVehicle_IdOrderByCompletedAtDesc(vehicleId);
        
        if (lastIntervention.isPresent() && lastIntervention.get().getKilometers() != null) {
            return lastIntervention.get().getKilometers();
        }
        
        return 0L;
    }
}
