package com.officina.officina_be.dto.response;

import java.time.LocalDate;
import java.util.Set;

import com.officina.officina_be.core.utils.Constants.BookingStatus;

public record InterventionHistoryResponse(
    Long id,
    Long kilometers,
    double cost,
    String notes,
    LocalDate completedAt,
    Long vehicleId,
    Long bookingId,
    Set<Long> jobsIds,
    BookingStatus status
) {
}
