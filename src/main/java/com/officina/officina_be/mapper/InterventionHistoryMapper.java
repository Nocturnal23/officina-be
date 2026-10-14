package com.officina.officina_be.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.officina.officina_be.dto.request.InterventionHistoryRequest;
import com.officina.officina_be.dto.response.InterventionHistoryResponse;
import com.officina.officina_be.dto.response.PageResponse;
import com.officina.officina_be.entity.InterventionHistory;
import com.officina.officina_be.entity.Jobs;

@Component 
public class InterventionHistoryMapper {
    public InterventionHistory toEntity(InterventionHistoryRequest request) {
        InterventionHistory interventionHistory = new InterventionHistory();
        interventionHistory.setCost(request.cost());
        interventionHistory.setNotes(request.notes());
        interventionHistory.setCompletedAt(request.completedAt());
        interventionHistory.setStatus(request.status());

        return interventionHistory;
    }

    public InterventionHistoryResponse toResponse(InterventionHistory entity) {
        Set<Long> jobsIds = entity.getJobs()
            .stream()
            .map(Jobs::getId)
            .collect(Collectors.toSet());

        Double safeCost = entity.getCost() != null ? entity.getCost() : 0;
        Long safeBookingId = entity.getBooking() != null ? entity.getBooking().getId() : null;

        return new InterventionHistoryResponse(
            entity.getId(),
            safeCost,
            entity.getNotes(),
            entity.getCompletedAt(),
            entity.getVehicle().getId(),
            safeBookingId,
            jobsIds,
            entity.getStatus()
        );
    }

    public PageResponse<InterventionHistoryResponse> toPageResponse(Page<InterventionHistory> page) {
        List<InterventionHistoryResponse> content = page.getContent()
            .stream()
            .map(this::toResponse)
            .toList();

        return new PageResponse<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }
}
