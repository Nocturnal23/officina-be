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
import com.officina.officina_be.dto.request.HistoryFilterRequest;
import com.officina.officina_be.dto.request.InterventionHistoryRequest;
import com.officina.officina_be.dto.response.InterventionHistoryResponse;
import com.officina.officina_be.dto.response.PageResponse;
import com.officina.officina_be.entity.Booking;
import com.officina.officina_be.entity.InterventionHistory;
import com.officina.officina_be.entity.Jobs;
import com.officina.officina_be.entity.Vehicle;
import com.officina.officina_be.error.BadRequest;
import com.officina.officina_be.error.ResourceNotFound;
import com.officina.officina_be.mapper.InterventionHistoryMapper;
import com.officina.officina_be.repository.BookingRepository;
import com.officina.officina_be.repository.InterventionHistoryRepository;
import com.officina.officina_be.repository.JobsRepository;
import com.officina.officina_be.repository.UserRepository;
import com.officina.officina_be.repository.VehicleRepository;
import com.officina.officina_be.repository.specification.InterventionSpecifications;

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
        Vehicle vehicle = vehicleRepo.findByIdAndDeletedFalseForUpdate(vehicleId)
                .orElseThrow(() -> new ResourceNotFound("Veicolo non trovato."));

        Booking booking = null;
        if (request.bookingId() != null) {
            booking = bookingRepo.findByIdAndDeletedFalseForUpdate(request.bookingId())
                    .orElseThrow(() -> new ResourceNotFound("Prenotazione non trovata."));

            if (!booking.getVehicle().getId().equals(vehicleId)) {
                throw new BadRequest("La prenotazione non appartiene al veicolo selezionato.");
            }

            var existingIntervention = interventionHistoryRepo
                    .findByBooking_IdAndDeletedFalse(booking.getId());
            if (existingIntervention.isPresent()) {
                InterventionHistory existing = existingIntervention.get();
                if (existing.getStatus() != request.status()) {
                    throw new BadRequest("Esiste già un intervento con uno stato diverso per questa prenotazione.");
                }
                if (booking.getStatus() != request.status()) {
                    validateBookingTransition(booking, request.status());
                    booking.setStatus(request.status());
                }
                return interventionHistoryMapper.toResponse(existing);
            }

            validateBookingTransition(booking, request.status());
        }

        Long interventionKilometers = request.interventionKilometers();
        if (interventionKilometers < vehicle.getKilometers()) {
            throw new BadRequest("I chilometri inseriti (" + interventionKilometers + ") sono inferiori a quelli attuali del veicolo (" + vehicle.getKilometers() + ").");
        }

        Set<Jobs> jobs = new HashSet<>(
                jobsRepo.findByIdInAndDeletedFalse(request.jobsIds()));

        InterventionHistory interventionHistory = interventionHistoryMapper.toEntity(request);
        interventionHistory.setVehicle(vehicle);
        interventionHistory.setJobs(jobs);
        if (request.bookingId() == null) {
            interventionHistory.setStatus(BookingStatus.COMPLETED);
        } else {
            booking.setStatus(request.status());
            interventionHistory.setBooking(booking);
            interventionHistory.setCustomerNotes(booking.getNotes());
        }
        vehicle.setKilometers(interventionKilometers);

        InterventionHistory savedIntervention = interventionHistoryRepo.save(interventionHistory);
        vehicleRepo.save(vehicle);

        return interventionHistoryMapper.toResponse(savedIntervention);
    }

    private void validateBookingTransition(Booking booking, BookingStatus targetStatus) {
        boolean validTransition = targetStatus != null && switch (targetStatus) {
            case REFUSED -> booking.getStatus() == BookingStatus.PENDING;
            case COMPLETED -> booking.getStatus() == BookingStatus.IN_PROGRESS;
            default -> false;
        };

        if (!validTransition) {
            throw new BadRequest("La prenotazione non può passare da "
                    + booking.getStatus() + " a " + targetStatus + " tramite la registrazione di un intervento.");
        }
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
    public PageResponse<InterventionHistoryResponse> getInterventionsHistoryByUserId(
            Long userId,
            int page,
            int size,
            List<BookingStatus> filterStatuses,
            HistoryFilterRequest filters) {
        userRepo.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFound("Utente non trovato."));

        if (filterStatuses == null || filterStatuses.isEmpty()) {
            filterStatuses = List.of(BookingStatus.COMPLETED, BookingStatus.REFUSED);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("completedAt").descending());
        Page<InterventionHistory> interventionPage = interventionHistoryRepo.findAll(
                InterventionSpecifications.history(userId, filterStatuses, filters),
                pageable);

        return interventionHistoryMapper.toPageResponse(interventionPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<InterventionHistoryResponse> getAllIntervention(
        int page,
        int size,
        List<BookingStatus> statuses,
        HistoryFilterRequest filters) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("completedAt").descending());
            Page<InterventionHistory> interventionPage = interventionHistoryRepo.findAll(
                InterventionSpecifications.history(null, statuses, filters),
                pageable
            );
            return interventionHistoryMapper.toPageResponse(interventionPage);
        }
}
