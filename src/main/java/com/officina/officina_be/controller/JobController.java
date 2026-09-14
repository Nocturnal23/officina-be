package com.officina.officina_be.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.officina.officina_be.dto.response.JobResponse;
import com.officina.officina_be.services.JobService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class JobController {
    private final JobService jobService;

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<JobResponse> getJobById(
        @PathVariable Long jobId
    ) {
        return ResponseEntity.ok(jobService.getJobById(jobId));
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getAvailableJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }
}
