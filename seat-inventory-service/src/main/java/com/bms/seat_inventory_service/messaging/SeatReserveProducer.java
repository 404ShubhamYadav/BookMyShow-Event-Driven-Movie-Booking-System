package com.bms.seat_inventory_service.messaging;

import com.bms.events.SeatReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.bms.common.KafkaConfigTopic.SEAT_RESERVED_TOPIC;

@Component
@Slf4j
@RequiredArgsConstructor
public class SeatReserveProducer {

    private final KafkaTemplate<String, SeatReservedEvent> template;

    public void publishSeatReserveEvent(SeatReservedEvent reservedEvent){
        try {
            log.info("SeatReserveProducer:: Publishing seatReserved event for bookingId {}", reservedEvent.bookingId());
            template.send(SEAT_RESERVED_TOPIC,reservedEvent.bookingId(), reservedEvent);
        }catch (Exception e) {
            log.error("SeatReserveProducer:: Error while publishing seatReserved event for bookingId {}: {}", reservedEvent.bookingId(), e.getMessage());
        }
    }
}
