package com.officina.officina_be.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.officina.officina_be.core.utils.Constants.BookingStatus;
import com.officina.officina_be.dto.response.InterventionHistoryResponse;
import com.officina.officina_be.dto.response.PageResponse;
import com.officina.officina_be.entity.User;
import com.officina.officina_be.services.InterventionHistoryService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor 
@RequestMapping("api/users/me")
public class InterventionHistoryController {
    private final InterventionHistoryService interventionHistoryService;

    @GetMapping("/history/interventions")
    public ResponseEntity<PageResponse<InterventionHistoryResponse>> getMyVehicleInterventions(
        @AuthenticationPrincipal User user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) List<BookingStatus> statuses
    ) {
        
        return ResponseEntity.ok(
        interventionHistoryService.getInterventionsHistoryByUserId(user.getId(), page, size, statuses)
    );
    }

}
