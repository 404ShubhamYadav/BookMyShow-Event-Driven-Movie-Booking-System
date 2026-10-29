package com.bms.seat_inventory_service.service;

import com.bms.events.SeatReservedEvent;
import com.bms.seat_inventory_service.entity.SeatInventory;
import com.bms.seat_inventory_service.enums.SeatStatus;
import com.bms.seat_inventory_service.messaging.SeatReserveProducer;
import com.bms.seat_inventory_service.repository.SeatInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatInventoryService {

    private final SeatInventoryRepository seatInventoryRepository;
    private final SeatReserveProducer seatReserveProducer;
    public void reserveSeats(SeatReservedEvent event) {
        log.info("SeatInventoryService:: Processing bookingCreated for bookingId {}", event.bookingId());

        // Fetch seat inventories for the given show and seat numbers
        List<SeatInventory> seats = seatInventoryRepository
                .findByShowIdAndSeatNumberIn(event.showId(), event.seatIds());

        // Check if all seats are available
        boolean allAvailable = seats.stream()
                .allMatch(s -> s.getStatus() == SeatStatus.AVAILABLE);

        if (allAvailable) {
            // Update seat status to LOCKED and set current booking ID
            seats.forEach(s -> {
                s.setStatus(SeatStatus.LOCKED);
                s.setCurrentBookingId(event.bookingId());
            });
            seatInventoryRepository.saveAll(seats);

            // Publish seat reserved event
            seatReserveProducer.publishSeatReserveEvent(new SeatReservedEvent(event.bookingId(), event.showId(), event.seatIds(), true, event.amount()));
            log.info("SeatInventoryService:: Seats locked successfully for bookingId {}", event.bookingId());

        } else{
            log.warn("SeatInventoryService:: Seat locking failed for bookingId {}. Some seats are not available.", event.bookingId());
            // Publish seat reserved event with failure
            seatReserveProducer
                    .publishSeatReserveEvent(new SeatReservedEvent(event.bookingId(), event.showId() ,event.seatIds(),false, event.amount()));
            }
    }

    public void rollbackSeatReservationOnFailure(String bookingId) {
        log.info("SeatInventoryService:: Releasing seats for bookingId {}", bookingId);
        List<SeatInventory> bookingSeats = seatInventoryRepository.findByCurrentBookingId(bookingId);

        bookingSeats.forEach(s -> {
            s.setStatus(SeatStatus.AVAILABLE);
            s.setCurrentBookingId(null);
        });

        seatInventoryRepository.saveAll(bookingSeats);
        log.info("SeatInventoryService:: Seats released successfully for bookingId {}", bookingId);

        //send failed event to downstream (booking-service)

        seatReserveProducer
                .publishSeatReserveEvent(new SeatReservedEvent(bookingId, null,null,false, 0));
    }
}
