package com.officina.officina_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.officina.officina_be.core.utils.Constants.BookingStatus;
import com.officina.officina_be.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    List<Booking> findByVehicle_IdAndDeletedFalse(Long vehicleId); //Insieme di prenotazioni sul veicolo.
    Page<Booking> findByUser_IdAndDeletedFalse(Long userId, Pageable pageable); //Insieme di prenotazioni dal cliente.
    Optional<Booking> findByIdAndDeletedFalse(Long bookingId); //Ottiene una singola prenotazione.
    Optional<Booking> findByIdAndUser_IdAndDeletedFalse(Long bookingId, Long userId);
    Page<Booking> findByDeletedFalse(Pageable pageable);
    Page<Booking> findByStatusInAndDeletedFalse(List<BookingStatus> statuses, Pageable pageable);
    Page<Booking> findByUser_IdAndStatusInAndDeletedFalse(Long userId, List<BookingStatus> statuses, Pageable pageable);
    
    /*
        PROJECTION (Proiezione basata su Interfaccia)
        
        Cos'è: Un "contenitore leggero" per i risultati di una query personalizzata.
        
        Perché è utile: La nostra query raggruppata non restituisce un'entità 'Booking' 
        completa, ma solo due colonne: lo stato e il conteggio. Invece di creare una 
        classe DTO, dichiariamo solo questa interfaccia.
        
        Come funziona: Spring Data JPA legge gli "alias" (la parola dopo l'AS) nella query SQL.
        Quando il DB risponde, Spring crea dinamicamente (tramite Proxy) un oggetto in RAM 
        che implementa questa interfaccia, riempiendo i getter 'getStatus()' e 'getCount()' 
        con i valori restituiti dal database.
    */
    interface BookingStatusCount {
        BookingStatus getStatus();
        Long getCount();
    }

    @Query("SELECT b.status as status, COUNT(b) as count FROM Booking b WHERE b.deleted = false GROUP BY b.status")
    List<BookingStatusCount> countBookingsByStatus();
}
