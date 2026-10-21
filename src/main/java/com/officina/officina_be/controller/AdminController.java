package com.officina.officina_be.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.officina.officina_be.core.utils.Constants.BookingStatus;
import com.officina.officina_be.dto.request.HistoryFilterRequest;
import com.officina.officina_be.dto.request.InterventionHistoryRequest;
import com.officina.officina_be.dto.request.JobAvailabilityRequest;
import com.officina.officina_be.dto.request.JobRequest;
import com.officina.officina_be.dto.response.BookingResponse;
import com.officina.officina_be.dto.response.InterventionHistoryResponse;
import com.officina.officina_be.dto.response.JobResponse;
import com.officina.officina_be.dto.response.PageResponse;
import com.officina.officina_be.dto.response.UserResponse;
import com.officina.officina_be.dto.response.VehicleResponse;
import com.officina.officina_be.services.BookingService;
import com.officina.officina_be.services.InterventionHistoryService;
import com.officina.officina_be.services.JobService;
import com.officina.officina_be.services.UserService;
import com.officina.officina_be.services.VehicleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor 
@RequestMapping("api/admin")
public class AdminController {
    private final VehicleService vehicleService;
    private final JobService jobService;
    private final BookingService bookingService;
    private final UserService userService;
    private final InterventionHistoryService interventionHistoryService;

    /*USER*/
    
    @GetMapping("/users")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String query
    ) {
        return ResponseEntity.ok(userService.getAllUsers(page, size, query));
    }

    /*VEHICLE*/

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleResponse>> getAllVehicles(
        @RequestParam(required = false) String plate
    ) {
        return ResponseEntity.ok(vehicleService.getAllVehicles(plate));
    }

    @GetMapping("vehicles/{vehicleId}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(vehicleService.getVehicleById(vehicleId));
    }

    /*JOB*/

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

    /*BOOKING*/

    @GetMapping("/bookings")
    public ResponseEntity<PageResponse<BookingResponse>> getAllBookings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) List<BookingStatus> statuses,
        @Valid @ModelAttribute HistoryFilterRequest filters
    ) {
        return ResponseEntity.ok(bookingService.getAllBookings(page, size, statuses, filters));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserByID(userId));
    }

    @PatchMapping("/bookings/{bookingId}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
        @PathVariable Long bookingId,
        @RequestParam BookingStatus status
    ) {
        return ResponseEntity.ok(bookingService.updateStatus(bookingId, status));
    }

    @DeleteMapping("/bookings/{bookingId}")
	public ResponseEntity<Void> deleteBooking(
		@PathVariable Long bookingId
	) {
		bookingService.AdminDeleteBooking(bookingId);
		return ResponseEntity.noContent().build();
	}

    @GetMapping("/bookings/summary")
    public ResponseEntity<Map<BookingStatus, Long>> getBookingSummary() {
        return ResponseEntity.ok(bookingService.getBookingStatusSummary());
    }

    /*INTERVENTION*/

    @PostMapping("/vehicles/{vehicleId}/interventions")
	public ResponseEntity<InterventionHistoryResponse> createIntervention(
		@PathVariable Long vehicleId,
		@Valid @RequestBody InterventionHistoryRequest request
	) {
		InterventionHistoryResponse response = interventionHistoryService.saveIntervention(
			request,
			vehicleId
		);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

    @PatchMapping("/interventions/{id}")
    public ResponseEntity<InterventionHistoryResponse> updateIntervention(
        @PathVariable Long id,
        @Valid @RequestBody InterventionHistoryRequest request
    ) {
        return ResponseEntity.ok(interventionHistoryService.updateIntervention(id, request));
    }

    @DeleteMapping("/interventions/{id}")
    public ResponseEntity<Void> deleteIntervention(@PathVariable Long id) {
        interventionHistoryService.deleteIntervention(id);
        return ResponseEntity.noContent().build();
    }
}
