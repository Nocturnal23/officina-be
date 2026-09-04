package com.officina.officina_be.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officina.officina_be.dto.request.BookingRequest;
import com.officina.officina_be.dto.response.BookingResponse;
import com.officina.officina_be.entity.Booking;
import com.officina.officina_be.entity.Jobs;
import com.officina.officina_be.entity.User;
import com.officina.officina_be.entity.Vehicle;
import com.officina.officina_be.error.ResourceNotFound;
import com.officina.officina_be.mapper.BookingMapper;
import com.officina.officina_be.repository.BookingRepository;
import com.officina.officina_be.repository.JobsRepository;
import com.officina.officina_be.repository.UserRepository;
import com.officina.officina_be.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final UserRepository userRepo;
    private final VehicleRepository vehicleRepo;
    private final BookingRepository bookingRepo;
    private final BookingMapper bookingMapper;
    private final JobsRepository jobsRepo;

    @Transactional
    public BookingResponse saveBooking(BookingRequest request, Long vehicleId, Long userId){
        Vehicle vehicle = vehicleRepo.findByIdAndUser_IdAndDeletedFalse(vehicleId, userId)
            .orElseThrow(() -> new ResourceNotFound("Veicolo non trovato."));
        User user = userRepo.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new ResourceNotFound("Utente non trovato."));

        Set<Jobs> jobs = new HashSet<>(
            jobsRepo.findByIdInAndDeletedFalse(request.jobsIds())
        );

        if (jobs.size() != request.jobsIds().size()) {
            throw new ResourceNotFound("Uno o più lavori non sono disponibili.");
        }

        Booking booking = bookingMapper.toEntity(request);
        booking.setVehicle(vehicle);
        booking.setUser(user);
        booking.setJobs(jobs);

        Booking savedBooking = bookingRepo.save(booking);

        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional
    public List<BookingResponse> getBookingByUserId(Long userId) {
        userRepo.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new ResourceNotFound("Utente non trovato."));

        return bookingRepo.findByUser_IdAndDeletedFalse(userId)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Transactional
    public List<BookingResponse> getBookingByVehicleId(Long vehicleId) {
        vehicleRepo.findByIdAndDeletedFalse(vehicleId)
            .orElseThrow(() -> new ResourceNotFound("Veicolo non trovato."));

        return bookingRepo.findByVehicle_IdAndDeletedFalse(vehicleId)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepo.findByIdAndUser_IdAndDeletedFalse(bookingId, userId)
            .orElseThrow(() -> new ResourceNotFound("Prenotazione non trovata."));

        booking.setDeleted(true);
        bookingRepo.save(booking);
    }
}
