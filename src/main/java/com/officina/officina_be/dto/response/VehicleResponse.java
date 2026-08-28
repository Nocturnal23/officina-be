package com.officina.officina_be.dto.response;

public record VehicleResponse(
    Long id,
    String brand,
    String model,
    String plate,
    int year,
    Long userId
) {}