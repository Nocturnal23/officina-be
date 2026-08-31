package com.officina.officina_be.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.officina.officina_be.dto.request.VehicleRequest;
import com.officina.officina_be.dto.response.VehicleResponse;
import com.officina.officina_be.services.VehicleService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    //Salva un veicolo. Da usare per clienti e admin.
    @PostMapping
    public ResponseEntity<VehicleResponse> addVehicle(
        @Valid @RequestBody VehicleRequest request
    ) {
        // TODO: sostituire con Principal/Authentication una volta introdotto JWT.
        VehicleResponse response = vehicleService.saveVehicle(request, 1L);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getVehiclesByUser() {
        // TODO: sostituire con Principal/Authentication una volta introdotto JWT.
        return ResponseEntity.ok(vehicleService.getVehiclesByUserId(1L));
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }
}
