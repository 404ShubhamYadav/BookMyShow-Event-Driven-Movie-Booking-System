package com.bms.booking_service.transformer;

import com.bms.booking_service.entity.Booking;
import com.bms.response.BookingResponse;

public class EntityToBookingResponseMapper {
    public static BookingResponse map(Booking booking) {
        return new BookingResponse(booking.getBookingCode(),
                booking.getStatus());
    }
}
