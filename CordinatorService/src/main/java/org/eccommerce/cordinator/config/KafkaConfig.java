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

    @Value("${CHECK_INVENTORY}")
    private String checkInventory;
    NewTopic placeOrderTopic()
    {
        return TopicBuilder
                .name(orderCreated)
                .build();
    }

    NewTopic checkInventory()
    {
        return TopicBuilder
                .name(checkInventory)
                .build();
    }


    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf) {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(pf);
        kafkaTemplate.setObservationEnabled(true);
        return kafkaTemplate;
    }


}
