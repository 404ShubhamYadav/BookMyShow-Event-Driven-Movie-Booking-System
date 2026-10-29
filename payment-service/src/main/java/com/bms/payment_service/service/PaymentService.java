package com.bms.payment_service.service;

import com.bms.events.BookingPaymentEvent;
import com.bms.payment_service.exception.PaymentServiceException;
import com.bms.payment_service.producer.PaymentEventsProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentEventsProducer eventsProducer;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void processPayment(BookingPaymentEvent event) throws PaymentServiceException {
        log.info("Processing payment event for bookingId: {}", event.bookingId());
        try {
            // Simulate payment failure scenario
            if (event.amount() > 2000) {
                log.info("Payment amount exceeds limit for bookingId: {}", event.bookingId());
                // failure events
                eventsProducer.publishPaymentFailureEvent(event);
                //throw new RuntimeException("Payment amount exceeds limit");
            } else {
                // success event
                eventsProducer.publishPaymentSuccessEvent(event);
                log.info("✅ Payment successful for bookingId: {}", event.bookingId());
            }
        } catch (Exception e) {
            log.error("❌ Payment failed for bookingId: {}. Reason: {}", event.bookingId(), e.getMessage());
            throw new PaymentServiceException("Payment processing failed for bookingId: " + event.bookingId());
        }
    }
}