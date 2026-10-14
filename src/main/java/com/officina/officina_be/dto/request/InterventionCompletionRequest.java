package com.officina.officina_be.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InterventionCompletionRequest(
    @NotNull @Valid InterventionHistoryRequest intervention,

    @NotNull(message = "Devi inserire il chilometraggio del veicolo")
    @PositiveOrZero(message = "Il chilometraggio non può essere negativo")
    Long vehicleKilometers
) {}
