package com.bms.booking_service.listener;

import com.bms.booking_service.service.BookingService;
import com.bms.events.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.bms.common.KafkaConfigTopic.MOVIE_BOOKING_EVENTS_TOPIC;
import static com.bms.common.KafkaConfigTopic.MOVIE_BOOKING_GROUP;

@Component
@Slf4j
@RequiredArgsConstructor
public class MovieBookingConsumer {

    private final BookingService bookingService;

    @KafkaListener(topics = MOVIE_BOOKING_EVENTS_TOPIC, groupId = MOVIE_BOOKING_GROUP)
    public void processBookingRequest(BookingCreatedEvent event){
        try {
            log.info("BookingListener:: Consuming booking event for id : {}",event.bookingId());
            bookingService.processBooking(event);
        }catch (Exception e){
            log.error("BookingListener:: Error while processing the booking event for id : {}", event.bookingId(), e);
        }
    }
}
