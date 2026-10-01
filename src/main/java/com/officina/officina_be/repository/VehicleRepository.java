package com.officina.officina_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.officina.officina_be.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    //Ricerca dei veicolo di un utente.
    List<Vehicle> findByUser_IdAndDeletedFalse(Long userId);

    //Elimina un veicolo.
    Optional<Vehicle> findByIdAndDeletedFalse(Long id);

    //Evita duplicati sulla targa.
    boolean existsByPlateIgnoreCase(String plate);

    boolean existsByPlateIgnoreCaseAndDeletedFalse(String plate);

    Optional<Vehicle> findByIdAndUser_IdAndDeletedFalse(Long vehicleId, Long userId);

    Optional<Vehicle> findByPlateIgnoreCase(String plate);
}