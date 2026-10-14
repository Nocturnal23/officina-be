package com.officina.officina_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record VehicleRequest(
    @NotBlank(message = "Devi inserire la marca")
    String brand,

    @NotBlank(message = "Devi inserire il modello")
    String model,

    @NotBlank(message = "Devi inserire la targa")
    @Size(max = 8, message = "La targa non può superare 8 caratteri (Es: AA000AA o CZ000000")
    String plate,
    
    @NotNull(message = "Devi inserire il chilometraggio attuale")
    @PositiveOrZero(message = "Il chilometraggio non può essere negativo")
    Long kilometers
) {}
