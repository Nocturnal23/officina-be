package com.officina.officina_be.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officina.officina_be.core.utils.Constants.BookingStatus;
import com.officina.officina_be.dto.request.BookingRequest;
import com.officina.officina_be.dto.response.BookingResponse;
import com.officina.officina_be.dto.response.PageResponse;
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
    public PageResponse<BookingResponse> getBookingByUserId(Long userId, int page, int size, List<BookingStatus> statuses) {
        userRepo.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new ResourceNotFound("Utente non trovato."));

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").ascending());

        Page<Booking> bookingPage;

        if (statuses == null || statuses.isEmpty()) {
            bookingPage = bookingRepo.findByUser_IdAndDeletedFalse(userId, pageable);
        } else {
            bookingPage = bookingRepo.findByUser_IdAndStatusInAndDeletedFalse(userId, statuses, pageable);
        }

       return bookingMapper.toPageResponse(bookingPage);
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

    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getAllBookings(int page, int size, List<BookingStatus> statuses) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending());

        Page<Booking> bookingPage;
        if (statuses == null || statuses.isEmpty()) {
            bookingPage = bookingRepo.findByDeletedFalse(pageable);
        } else {
            bookingPage = bookingRepo.findByStatusInAndDeletedFalse(statuses, pageable);
        }

        return bookingMapper.toPageResponse(bookingPage);
    }

    @Transactional
    public BookingResponse updateStatus(Long bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepo.findByIdAndDeletedFalse(bookingId)
                .orElseThrow(() -> new ResourceNotFound("Prenotazione con ID " + bookingId + " non trovata."));

        booking.setStatus(newStatus);
        Booking updated = bookingRepo.save(booking);

        return bookingMapper.toResponse(updated);
    }

    @Transactional
    public void AdminDeleteBooking(Long bookingId) {
        Booking booking = bookingRepo.findByIdAndDeletedFalse(bookingId)
            .orElseThrow(() -> new ResourceNotFound("Prenotazione non trovata."));

        booking.setDeleted(true);
        bookingRepo.save(booking);
    }

    @Transactional
    public PageResponse<BookingResponse> getBookingHistoryByUserId(Long userId, int page, int size, List<BookingStatus> filterStatuses) {
        userRepo.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new ResourceNotFound("Utente non trovato."));

        if (filterStatuses == null || filterStatuses.isEmpty()) {
            filterStatuses = List.of(BookingStatus.COMPLETED, BookingStatus.REFUSED);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending());
        Page<Booking> bookingPage = bookingRepo.findByUser_IdAndStatusInAndDeletedFalse(userId, filterStatuses, pageable);

        return bookingMapper.toPageResponse(bookingPage);
    }
}
