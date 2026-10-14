package com.officina.officina_be.mapper;

import org.springframework.stereotype.Component;

import com.officina.officina_be.dto.request.VehicleRequest;
import com.officina.officina_be.dto.response.VehicleResponse;
import com.officina.officina_be.entity.Vehicle;

@Component
public class VehicleMapper {
    public Vehicle toEntity(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(request.brand());
        vehicle.setModel(request.model());
        vehicle.setKilometers(request.kilometers());
        vehicle.setPlate(request.plate().toUpperCase());
        return vehicle;
    }

    public VehicleResponse toResponse(Vehicle entity) {
        return new VehicleResponse(
            entity.getId(),
            entity.getBrand(),
            entity.getModel(),
            entity.getPlate(),
            entity.getKilometers(),
            entity.getUser().getId()
        );
    }
}
