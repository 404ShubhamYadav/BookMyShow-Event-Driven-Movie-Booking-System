package com.bms.events;

import java.util.List;

public record BookingCreatedEvent(String bookingId,
                                  String userId,
                                  String showId,
                                  List<String> seatIds,
                                  long amount,
                                  String status) {}
