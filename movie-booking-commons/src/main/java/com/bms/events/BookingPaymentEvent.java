package com.bms.events;

import java.util.List;

public record BookingPaymentEvent(String bookingId, String showId, List<String> seatIds, boolean paymentCompleted, long amount) {
}
