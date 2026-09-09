package com.officina.officina_be.dto.response;

import java.time.LocalDate;
import java.util.Set;

import com.officina.officina_be.core.utils.Constants.BookingStatus;

public record BookingResponse(
    Long id,
    BookingStatus status,
    String notes,
    Long vehicleId,
    Long userId,
    Set<Long> jobsIds,
    LocalDate appointmentDate
) {}
