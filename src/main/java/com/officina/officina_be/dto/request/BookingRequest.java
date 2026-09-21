package com.officina.officina_be.dto.request;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookingRequest(
    @Size(max = 500, message = "Il messaggio non può eccedere i 500 caratteri!")
    String notes,
    
    @NotNull
    Set<Long> jobsIds,

    @NotNull 
    LocalDate appointmentDate
) {}
