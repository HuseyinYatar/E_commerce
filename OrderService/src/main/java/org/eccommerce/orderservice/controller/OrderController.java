package org.eccommerce.orderservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.eccommerce.orderservice.dto.OrderResponse;
import org.eccommerce.orderservice.dto.OrderSaveRequest;
import org.eccommerce.orderservice.dto.StartOrderPlacedEvent;
import org.eccommerce.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${ORDER_API}")
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${ORDER_CREATED}")
    private String orderCreated;

    public OrderController(OrderService orderService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("${ORDER_PLACE}")
    public String placeOrder(@RequestBody OrderSaveRequest orderSaveRequest) {
        log.info("Order save request recieved  orderId:{}", orderSaveRequest.getOrderId());

        StartOrderPlacedEvent startOrderPlacedEvent = orderService.OrderPlace(orderSaveRequest);

        kafkaTemplate.send(orderCreated, startOrderPlacedEvent);
        log.info("PlaceOrder event Started  orderId:{}", orderSaveRequest.getOrderId());
        return "Order Placed";
    }

    @GetMapping("${GET_ALL_ORDER}")
    public List<OrderResponse> getAllOrder() {
        return orderService.getAllOrder();
    }
}
