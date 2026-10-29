package com.bms.booking_service.transformer;

import com.bms.booking_service.entity.Booking;
import com.bms.events.BookingCreatedEvent;
import com.bms.request.BookingRequest;

import java.util.UUID;

public class BookingRequestToEntityMapper {

    public static Booking bookingRequestToBooking(BookingRequest request){
        var reservationCode = UUID.randomUUID().toString().split("-")[0];
        Booking booking = new Booking();
        booking.setId(System.currentTimeMillis());
        booking.setShowId(request.showId());
        booking.setBookingCode(reservationCode);
        booking.setSeatIds(request.seatIds());
        booking.setUserId(request.userId());
        booking.setStatus("CONFIRMED");
        booking.setCreatedAt(java.time.Instant.now());
        booking.setAmount(request.amount());
        return booking;
    }
    public static Booking mapEvents(BookingCreatedEvent request) {
        Booking booking = new Booking();
        booking.setId(System.currentTimeMillis());
        booking.setShowId(request.showId());
        booking.setBookingCode(request.bookingId());
        booking.setSeatIds(request.seatIds());
        booking.setUserId(request.userId());
        booking.setStatus(request.status());
        booking.setCreatedAt(java.time.Instant.now());
        booking.setAmount(request.amount());
        return booking;
    }
}
