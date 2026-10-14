package com.officina.officina_be.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.officina.officina_be.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUser_IdAndDeletedFalse(Long userId);

    Optional<Vehicle> findByIdAndDeletedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select vehicle from Vehicle vehicle where vehicle.id = :id and vehicle.deleted = false")
    Optional<Vehicle> findByIdAndDeletedFalseForUpdate(@Param("id") Long id);

    boolean existsByPlateIgnoreCase(String plate);

    boolean existsByPlateIgnoreCaseAndDeletedFalse(String plate);

    Optional<Vehicle> findByIdAndUser_IdAndDeletedFalse(Long vehicleId, Long userId);

    Optional<Vehicle> findByPlateIgnoreCase(String plate);
}