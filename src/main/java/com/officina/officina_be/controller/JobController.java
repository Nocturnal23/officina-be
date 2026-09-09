package com.officina.officina_be.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.officina.officina_be.dto.request.JobRequest;
import com.officina.officina_be.dto.response.JobResponse;
import com.officina.officina_be.entity.Jobs;
import com.officina.officina_be.services.JobService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class JobController {
    private final JobService jobService;

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<JobResponse> getJobById(
        @PathVariable Long jobId
    ) {
        return ResponseEntity.ok(jobService.getJobById(jobId));
    }

    @PostMapping("/admin/jobs")
    public ResponseEntity<JobResponse> registerJob(
        @Valid @RequestBody JobRequest request
    ) {
        JobResponse response = jobService.registerService(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/admin/jobs/{jobId}")
    public ResponseEntity<JobResponse> updateJob(
        @PathVariable Jobs job,
        @RequestBody Map<String, Object> updates
    ) {
        JobResponse response = jobService.updateJob(job.getId(), updates);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/jobs/{jobId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Jobs job) {
        jobService.deleteJob(job.getId());

        return ResponseEntity.noContent().build();
    }
}
