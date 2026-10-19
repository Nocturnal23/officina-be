package com.officina.officina_be.services;

import java.util.List;
import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officina.officina_be.dto.request.VehicleRequest;
import com.officina.officina_be.dto.response.VehicleResponse;
import com.officina.officina_be.entity.User;
import com.officina.officina_be.entity.Vehicle;
import com.officina.officina_be.error.BadRequest;
import com.officina.officina_be.error.DuplicateResource;
import com.officina.officina_be.error.ResourceNotFound;
import com.officina.officina_be.mapper.VehicleMapper;
import com.officina.officina_be.repository.UserRepository;
import com.officina.officina_be.repository.VehicleRepository;
import com.officina.officina_be.repository.specification.VehicleSpecifications;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    private final VehicleMapper vehicleMapper;

    @Transactional
    public VehicleResponse saveVehicle(VehicleRequest request, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("Utente con ID " + userId + " non trovato."));

        Vehicle vehicle = vehicleRepo.findByPlateIgnoreCase(request.plate())
                .map(existing -> {
                    if (!existing.isDeleted()) {
                        throw new DuplicateResource(
                                "Attenzione, la targa inserita è già associata a un altro veicolo attivo.");
                    }

                    existing.setUser(user);
                    existing.setDeleted(false);
                    existing.setBrand(request.brand());
                    existing.setModel(request.model());
                    existing.setKilometers(request.kilometers());

                    return existing;
                })
                .orElseGet(() -> {
                    Vehicle newVehicle = vehicleMapper.toEntity(request);
                    newVehicle.setUser(user);
                    return newVehicle;
                });

        Vehicle savedVehicle = vehicleRepo.save(vehicle);
        return vehicleMapper.toResponse(savedVehicle);
    }

    @Transactional
    public List<VehicleResponse> getVehiclesByUserId(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFound("Utente con ID " + userId + " non trovato.");
        }

        return vehicleRepo.findByUser_IdAndDeletedFalse(userId)
                .stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> searchVehiclesForUserHistory(Long userId, String query) {
        if (query == null || query.isBlank()) {
            throw new BadRequest("Specificare almeno un termine di ricerca.");
        }

        List<String> terms = Arrays.stream(query.trim().toLowerCase().split("\\s+"))
                .distinct()
                .toList();

        return vehicleRepo.findAll(
                        VehicleSpecifications.searchForUserHistory(userId, terms),
                        Sort.by("brand").ascending().and(Sort.by("model").ascending()).and(Sort.by("plate").ascending()))
                .stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles(String plate) {
        List<Vehicle> vehicles = plate == null || plate.isBlank()
                ? vehicleRepo.findAll()
                : vehicleRepo.findByPlateIgnoreCase(plate.trim()).stream().toList();

        return vehicles.stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteVehicle(Long vehicleId, Long userId) {
        Vehicle vehicle = vehicleRepo.findByIdAndUser_IdAndDeletedFalse(vehicleId, userId)
                .orElseThrow(() -> new ResourceNotFound("Veicolo con ID " + vehicleId + " non trovato."));

        vehicle.setDeleted(true);
        vehicleRepo.save(vehicle);
    }

    @Transactional
    public VehicleResponse getVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepo.findByIdAndDeletedFalse(vehicleId)
                .orElseThrow(() -> new ResourceNotFound("Veicolo con ID " + vehicleId + " non trovato."));
        return vehicleMapper.toResponse(vehicle);
    }
}
