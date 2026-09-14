package com.officina.officina_be.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officina.officina_be.dto.request.VehicleRequest;
import com.officina.officina_be.dto.response.VehicleResponse;
import com.officina.officina_be.entity.User;
import com.officina.officina_be.entity.Vehicle;
import com.officina.officina_be.error.DuplicateResource;
import com.officina.officina_be.error.ResourceNotFound;
import com.officina.officina_be.mapper.VehicleMapper;
import com.officina.officina_be.repository.UserRepository;
import com.officina.officina_be.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    private final VehicleMapper vehicleMapper;
     
    @Transactional
    public VehicleResponse saveVehicle(VehicleRequest request, Long userId) {
        if(vehicleRepo.existsByPlateIgnoreCaseAndDeletedFalse(request.plate())) {
            throw new DuplicateResource("Attenzione, la targa inserita è già associata a un altro veicolo");
        }

        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFound("Utente con ID " + userId + " non trovato."));
        
        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setUser(user);

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
