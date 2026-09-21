package com.officina.officina_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.officina.officina_be.core.utils.Constants.BookingStatus;
import com.officina.officina_be.entity.InterventionHistory;

@Repository 
public interface InterventionHistoryRepository extends JpaRepository<InterventionHistory, Long>{
    Optional<InterventionHistory> findByIdAndDeletedFalse(Long id);
    // List<InterventionHistory> findByVehicle_IdAndDeletedFalse(Long vehicleId);
    Page<InterventionHistory> findByVehicle_IdAndDeletedFalse(Long vehicleId, Pageable pageable);
    Page<InterventionHistory> findByVehicle_IdAndStatusInAndDeletedFalse(Long vehicleId, List<BookingStatus> statuses, Pageable pageable);
    //TODO: Aggiungere altro eventualmente.
}
