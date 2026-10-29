package com.bms.booking_orchestrator.controller;

import com.bms.booking_orchestrator.service.MovieBookingOrchestrator;
import com.bms.request.BookingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orchestrator")
public class MovieBookingOrchestratorController {

    private final MovieBookingOrchestrator orchestrator;

    @PostMapping("/bookings")
    public String startSaga(@RequestBody BookingRequest request) {
        return orchestrator.createBooking(request);
    }
}
