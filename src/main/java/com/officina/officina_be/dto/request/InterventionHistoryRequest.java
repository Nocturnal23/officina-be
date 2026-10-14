package com.officina.officina_be.dto.request;

import java.time.LocalDate;
import java.util.Set;

import com.officina.officina_be.core.utils.Constants.BookingStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record InterventionHistoryRequest( 
    Double cost,

    @Size(max = 500, message = "Il messaggio non può eccedere i 500 caratteri!")
    String notes,

    LocalDate completedAt,

    @NotNull(message = "Devi inserire il chilometraggio attuale")
    @PositiveOrZero(message = "Il chilometraggio non può essere negativo")
    Long interventionKilometers,

    Set<Long> jobsIds,

    Long bookingId,
    
    BookingStatus status
) {
}