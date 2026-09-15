package com.officina.officina_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.officina.officina_be.core.utils.Constants.BookingStatus;
import com.officina.officina_be.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByVehicle_IdAndDeletedFalse(Long vehicleId); //Insieme di prenotazioni sul veicolo.
    List<Booking> findByUser_IdAndDeletedFalse(Long userId); //Insieme di prenotazioni dal cliente.
    Optional<Booking> findByIdAndDeletedFalse(Long bookingId); //Ottiene una singola prenotazione.
    Optional<Booking> findByIdAndUser_IdAndDeletedFalse(Long bookingId, Long userId);
    List<Booking> findByDeletedFalse();
    List<Booking> findByStatusAndDeletedFalse(BookingStatus status);
}
