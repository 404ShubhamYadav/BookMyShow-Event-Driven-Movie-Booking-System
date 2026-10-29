package com.bms.payment_service.consumer;

import com.bms.events.BookingPaymentEvent;
import com.bms.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.bms.common.KafkaConfigTopic.PAYMENT_EVENTS_CMD_TOPIC;
import static com.bms.common.KafkaConfigTopic.PAYMENT_EVENT_GROUP;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = PAYMENT_EVENTS_CMD_TOPIC, groupId =PAYMENT_EVENT_GROUP)
    public void onPaymentEvents(BookingPaymentEvent event){
        try {
            log.info("PaymentEventListener:: Processing payment events");
            paymentService.processPayment(event);
        }catch (Exception e){
            log.error("Error processing payment event for bookingId {}: {}", event.bookingId(), e.getMessage());
        }
    }
}
