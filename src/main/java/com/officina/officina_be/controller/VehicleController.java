package com.officina.officina_be.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.officina.officina_be.dto.request.VehicleRequest;
import com.officina.officina_be.dto.response.VehicleResponse;
import com.officina.officina_be.entity.User;
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
        @AuthenticationPrincipal User user,
        @Valid @RequestBody VehicleRequest request
    ) {
        VehicleResponse response = vehicleService.saveVehicle(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getVehiclesByUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(vehicleService.getVehiclesByUserId(user.getId()));
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long vehicleId, @AuthenticationPrincipal User user) {
        vehicleService.deleteVehicle(vehicleId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
