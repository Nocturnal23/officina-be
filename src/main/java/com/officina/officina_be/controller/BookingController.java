package com.officina.officina_be.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.officina.officina_be.core.utils.Constants.BookingStatus;
import com.officina.officina_be.dto.request.BookingRequest;
import com.officina.officina_be.dto.response.BookingResponse;
import com.officina.officina_be.dto.response.PageResponse;
import com.officina.officina_be.entity.User;
import com.officina.officina_be.services.BookingService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/vehicles/{vehicleId}/bookings")
	public ResponseEntity<BookingResponse> createBooking(
		@AuthenticationPrincipal User user,
		@PathVariable Long vehicleId,
		@Valid @RequestBody BookingRequest request
	) {
		BookingResponse response = bookingService.saveBooking(
			request,
			vehicleId,
			user.getId()
		);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

    @GetMapping("/bookings")
    public ResponseEntity<PageResponse<BookingResponse>> getBookings(
        @AuthenticationPrincipal User user,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) List<BookingStatus> statuses
    ) {
        return ResponseEntity.ok(
			bookingService.getBookingByUserId(user.getId(), page, size, statuses)
    	);
    }

    @DeleteMapping("/bookings/{bookingId}")
	public ResponseEntity<Void> deleteBooking(
		@AuthenticationPrincipal User user,
		@PathVariable Long bookingId
	) {
		bookingService.deleteBooking(bookingId, user.getId());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/history")
	public ResponseEntity<PageResponse<BookingResponse>> getHistoricalBookings(
		@AuthenticationPrincipal User user,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) List<BookingStatus> statuses
	) {
		return ResponseEntity.ok(
			bookingService.getBookingHistoryByUserId(user.getId(), page, size, statuses)
		);
	}
}
