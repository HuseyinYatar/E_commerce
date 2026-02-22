package org.eccommerce.cordinator.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.JacksonJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class KafkaConfig {


    //TOPICS
    @Value("${ORDER_CREATED}")
    private String orderCreated;


    @Bean
    NewTopic placeOrderTopic() {
        return TopicBuilder
                .name(orderCreated)
                .build();
    }

    @Value("${CHECK_INVENTORY}")
    private String checkInventory;

    @Bean
    NewTopic checkInventory() {
        return TopicBuilder
                .name(checkInventory)
                .build();
    }

    @Value("${CHECKED_INVENTORY}")
    private String checkedInventory;

    @Bean
    NewTopic checkedInventory() {
        return TopicBuilder
                .name(checkedInventory)
                .build();
    }

    @Value("${START_PAYMENT}")
    private String startPaymentEvent;

    @Bean
    NewTopic startPaymentEvent() {
        return TopicBuilder
                .name(startPaymentEvent)
                .build();
    }

    @Value("${FINISH_PAYMENT}")
    private String finishedPaymentEvent;

    @Bean
    NewTopic finishedPaymentEvent() {
        return TopicBuilder
                .name(finishedPaymentEvent)
                .build();
    }

    @Value("${FAIL_PAYMENT}")
    private String failedPayment;

    @Bean
    NewTopic failedPayment() {
        return TopicBuilder
                .name(failedPayment)
                .build();
    }



    @Bean
    KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> pf) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(pf);
        kafkaTemplate.setObservationEnabled(true);
        return kafkaTemplate;
    }

    @Bean
    public RecordMessageConverter smartConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}



