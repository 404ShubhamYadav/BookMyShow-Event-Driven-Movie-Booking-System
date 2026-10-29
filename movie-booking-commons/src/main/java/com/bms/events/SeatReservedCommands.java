package com.bms.events;

import java.util.List;

public record SeatReservedCommands(String bookingId, String showId, List<String> seatIds, boolean reserved, long amount) {
}
