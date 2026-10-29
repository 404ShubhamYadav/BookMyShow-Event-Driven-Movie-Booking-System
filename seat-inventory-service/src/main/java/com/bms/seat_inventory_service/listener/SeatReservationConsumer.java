package com.bms.seat_inventory_service.listener;

import com.bms.events.SeatReservedEvent;
import com.bms.seat_inventory_service.service.SeatInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.bms.common.KafkaConfigTopic.SEAT_EVENT_GROUP;
import static com.bms.common.KafkaConfigTopic.SEAT_RESERVED_CMD_TOPIC;

@Component
@Slf4j
@RequiredArgsConstructor
public class SeatReservationConsumer {

    private final SeatInventoryService service;

    @KafkaListener(topics = SEAT_RESERVED_CMD_TOPIC, groupId = SEAT_EVENT_GROUP)
    public void onSeatReserveEvent(SeatReservedEvent event){
        log.info("SeatReserveEventListener:: Consuming the event");

        if(event.reserved()){
            //book the seat
            service.reserveSeats(event);
            log.info("SeatReserveEventListener:: Seats reserved successfully for bookingId {}", event.bookingId());
        }else{
            //rollback
            service.rollbackSeatReservationOnFailure(event.bookingId());
            log.warn("SeatReserveEventListener:: Seat reservation failed for bookingId {}. Rolling back any locked seats.", event.bookingId());

        }
    }
}
