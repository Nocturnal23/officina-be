package com.officina.officina_be.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.officina.officina_be.dto.request.BookingRequest;
import com.officina.officina_be.dto.response.BookingResponse;
import com.officina.officina_be.entity.Booking;
import com.officina.officina_be.entity.Jobs;

@Component
public class BookingMapper {
    public Booking toEntity(BookingRequest request) {
        Booking booking = new Booking();
        booking.setNotes(request.notes());
        booking.setAppointmentDate(request.appointmentDate());
        return booking;
    }

    public BookingResponse toResponse(Booking entity) {
        Set<Long> jobsIds = entity.getJobs()
            .stream()
            .map(Jobs::getId)
            .collect(Collectors.toSet());

        return new BookingResponse(
            entity.getId(),
            entity.getStatus(),
            entity.getNotes(),
            entity.getVehicle().getId(),
            entity.getUser().getId(),
            jobsIds,
            entity.getAppointmentDate()
        );
    }
}
