package com.bms.booking_service.service;

import com.bms.booking_service.entity.Booking;
import com.bms.booking_service.repository.BookingRepository;
import com.bms.booking_service.transformer.BookingRequestToEntityMapper;
import com.bms.events.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    public void processBooking(BookingCreatedEvent event) {
        log.info("BookingService:: Processing booking for user {} for show {}", event.userId(), event.showId());

        Booking booking = bookingRepository.findByBookingCode(event.bookingId())
                .map(existing ->updateExistingBooking(existing, event))
                .orElseGet(() -> createBooking(event));

        Booking saved = bookingRepository.save(booking);
        log.info(
                "Booking saved: reservation id {} | status {}",
                saved.getBookingCode(), saved.getStatus()
        );
    }

    private Booking createBooking(BookingCreatedEvent event) {
        log.info("Creating new booking for id {}", event.bookingId());
        return BookingRequestToEntityMapper.mapEvents(event);
    }
    private Booking updateExistingBooking(Booking existing, BookingCreatedEvent event) {
        log.info("Updating booking for id {}", event.bookingId());
        existing.setStatus(event.status());
        return existing;
    }
}
