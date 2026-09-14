package com.officina.officina_be.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.officina.officina_be.dto.request.JobAvailabilityRequest;
import com.officina.officina_be.dto.request.JobRequest;
import com.officina.officina_be.dto.response.JobResponse;
import com.officina.officina_be.dto.response.VehicleResponse;
import com.officina.officina_be.services.JobService;
import com.officina.officina_be.services.VehicleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor 
@RequestMapping("api/admin")
public class AdminController {
    private final VehicleService vehicleService;
    private final JobService jobService;

    @GetMapping("vehicles/{vehicleId}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(vehicleService.getVehicleById(vehicleId));
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> registerJob(
        @Valid @RequestBody JobRequest request
    ) {
        JobResponse response = jobService.registerService(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/jobs/{jobId}")
    public ResponseEntity<JobResponse> updateJob(
        @PathVariable Long jobId,
        @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(
            jobService.updateJob(jobId, updates)
        );
    }

    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getAdminJobs() {
        return ResponseEntity.ok(jobService.getAllJobsForAdmin());
    }

    @PatchMapping("/jobs/{jobId}/availability")
    public ResponseEntity<JobResponse> updateAvailability(@PathVariable Long jobId, @RequestBody JobAvailabilityRequest request) {
        return ResponseEntity.ok(
            jobService.updateAvailability(jobId, request)
        );
    }
}
