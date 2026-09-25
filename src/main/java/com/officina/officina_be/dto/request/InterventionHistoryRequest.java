package com.officina.officina_be.dto.request;

import java.time.LocalDate;
import java.util.Set;

import com.officina.officina_be.core.utils.Constants.BookingStatus;

import jakarta.validation.constraints.Size;

public record InterventionHistoryRequest( 
    Long kilometers,

    double cost,

    @Size(max = 500, message = "Il messaggio non può eccedere i 500 caratteri!")
    String notes,

    LocalDate completedAt,

    Set<Long> jobsIds,

    Long bookingId,
    
    BookingStatus status
) {
}