package com.bms.booking_orchestrator.service;

import com.bms.events.BookingCreatedEvent;
import com.bms.events.BookingPaymentEvent;
import com.bms.events.SeatReservedEvent;
import com.bms.request.BookingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.bms.common.KafkaConfigTopic.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieBookingOrchestrator {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    public String createBooking(BookingRequest request) {
        // Send booking-created event
        String bookingId = generateBookingId();
        BookingCreatedEvent bookingCreatedEvent = buildBookingCreateEvents(request, bookingId);

        kafkaTemplate.send(MOVIE_BOOKING_EVENTS_TOPIC, bookingId, bookingCreatedEvent);
        log.info("MovieBookingOrchestrator:: Published bookingCreatedEvent for bookingId: {}", bookingId);

        // Prepare and send seat-reserve command
        SeatReservedEvent seatReservedEvent = new SeatReservedEvent(bookingId, request.showId(), request.seatIds(), true, request.amount());
        kafkaTemplate.send(SEAT_RESERVED_CMD_TOPIC, bookingId, seatReservedEvent);
        log.info("MovieBookingOrchestrator:: Published seatReserve request for bookingId: {}", bookingId);

        return bookingId;
    }
    private BookingCreatedEvent buildBookingCreateEvents(BookingRequest request, String bookingId) {
        return new BookingCreatedEvent(
                bookingId,
                request.userId(),
                request.showId(),
                request.seatIds(),
                request.amount(),
                "PENDING"
        );
    }

    @KafkaListener(topics = SEAT_RESERVED_TOPIC, groupId = ORCHESTRATOR_CONSUMER_GROUP)
    public void onSeatReserve(SeatReservedEvent event){
        log.info("MovieBookingOrchestrator:: Consuming seatReserved event for bookingId: {}", event.bookingId());

        if(event.reserved()){
            sendPaymentRequest(event);
            log.info("Orchestrator published BookingPaymentEvent (request) to {}", "Payment-Service");
        }else{
            sendRollbackToBookingService(event);
            log.info("Orchestrator published BookingCreatedEvent (FAILED) to {}", "Booking-Service");
        }
    }

    private void sendPaymentRequest(SeatReservedEvent event) {
        BookingPaymentEvent paymentEvent = new BookingPaymentEvent(event.bookingId(),event.showId(),event.seatIds(), false, event.amount());
        kafkaTemplate.send(PAYMENT_EVENTS_CMD_TOPIC, event.bookingId(), paymentEvent);
    }

    private void sendRollbackToBookingService(SeatReservedEvent event) {
        BookingCreatedEvent bookingFailureEvent = new BookingCreatedEvent(
                event.bookingId(),
                null,
                event.showId(),
                event.seatIds(),
                event.amount(),
                "FAILED"
        );
        kafkaTemplate.send(MOVIE_BOOKING_EVENTS_TOPIC, event.bookingId(), bookingFailureEvent);
    }

    @KafkaListener(topics = PAYMENT_EVENTS_TOPIC, groupId = ORCHESTRATOR_CONSUMER_GROUP)
    public void onPaymentStatus(BookingPaymentEvent event){
        log.info("MovieBookingOrchestrator:: Consuming payment event for bookingId: {}", event.bookingId());
        if(event.paymentCompleted()){
            //update booking status to confirm
            confirmBookingStatus(event);
            log.info("Orchestrator published BookingCreatedEvent (CONFIRMED)");

        }else{
            //send rollback cmd to seat inventory service
            SeatReservedEvent seatFailureEvent = new SeatReservedEvent(event.bookingId(), event.showId() ,event.seatIds(),false, event.amount());
            kafkaTemplate.send(SEAT_RESERVED_CMD_TOPIC, event.bookingId(), seatFailureEvent);
            log.info("Orchestrator published SeatReservedEvent (release/failed) to {}", "Seat-Service");
        }
    }

    private void confirmBookingStatus(BookingPaymentEvent event) {
        BookingCreatedEvent bookingSuccessEvent = new BookingCreatedEvent(
                event.bookingId(),
                null,
                event.showId(),
                event.seatIds(),
                event.amount(),
                "CONFIRMED"
        );
        kafkaTemplate.send(MOVIE_BOOKING_EVENTS_TOPIC, event.bookingId(), bookingSuccessEvent);
    }



    private String generateBookingId() {
        return UUID.randomUUID().toString().split("-")[0];
    }
}
