package com.bms.payment_service.producer;

import com.bms.events.BookingPaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import static com.bms.common.KafkaConfigTopic.PAYMENT_EVENTS_TOPIC;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventsProducer {

    private final KafkaTemplate<String, Object> template;

    public void publishPaymentSuccessEvent(BookingPaymentEvent event){
        log.info("Publishing payment success event ...");
        BookingPaymentEvent paymentEvent=new BookingPaymentEvent(event.bookingId(),event.showId(),event.seatIds(),true,event.amount());
        template.send(PAYMENT_EVENTS_TOPIC, event.bookingId(),paymentEvent);
    }

    public void publishPaymentFailureEvent(BookingPaymentEvent event){
        log.info("Publishing payment failure event ...");
        BookingPaymentEvent paymentEvent=new BookingPaymentEvent(event.bookingId(),event.showId() ,event.seatIds(),false,event.amount());
        template
                .send(PAYMENT_EVENTS_TOPIC, event.bookingId(),paymentEvent);

    }
}
