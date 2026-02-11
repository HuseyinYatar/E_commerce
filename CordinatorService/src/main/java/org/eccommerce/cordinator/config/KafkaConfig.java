package org.eccommerce.cordinator.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {


    //TOPICS
    @Value("${ORDER_CREATED}")
    private String orderCreated;


    NewTopic placeOrderTopic() {
        return TopicBuilder
                .name(orderCreated)
                .build();
    }

    @Value("${CHECK_INVENTORY}")
    private String checkInventory;

    NewTopic checkInventory() {
        return TopicBuilder
                .name(checkInventory)
                .build();
    }

    @Value("${CHECKED_INVENTORY}")
    private String checkedInventory;

    NewTopic checkedInventory() {
        return TopicBuilder
                .name(checkedInventory)
                .build();
    }

    @Value("${START_PAYMENT}")
    private String startPaymentEvent;

    NewTopic startPaymentEvent() {
        return TopicBuilder
                .name(startPaymentEvent)
                .build();
    }

    @Value("${FINISHED_PAYMENT}")
    private String finishedPaymentEvent;

    NewTopic finishedPaymentEvent() {
        return TopicBuilder
                .name(finishedPaymentEvent)
                .build();
    }

    @Value("${FAILED_PAYMENT}")
    private String failedPayment;

    NewTopic failedPayment() {
        return TopicBuilder
                .name(failedPayment)
                .build();
    }

    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf) {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(pf);
        kafkaTemplate.setObservationEnabled(true);
        return kafkaTemplate;
    }


}
