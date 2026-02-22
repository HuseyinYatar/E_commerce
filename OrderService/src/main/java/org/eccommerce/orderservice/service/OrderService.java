package org.eccommerce.orderservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eccommerce.orderservice.dto.OrderResponse;
import org.eccommerce.orderservice.dto.OrderSaveRequest;
import org.eccommerce.orderservice.dto.StartOrderPlacedEvent;
import org.eccommerce.orderservice.exception.OrderNotFound;
import org.eccommerce.orderservice.mapper.OrderEventMapper;
import org.eccommerce.orderservice.mapper.OrderMapper;
import org.eccommerce.orderservice.model.Order;
import org.eccommerce.orderservice.model.enums.OrderStatus;
import org.eccommerce.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderEventMapper orderEventMapper;
    private final OutboxService outboxService;


    @Transactional
    public StartOrderPlacedEvent OrderPlace(OrderSaveRequest orderSaveRequest) {

        Order order = orderMapper.requestToEntity(orderSaveRequest);
        orderRepository.save(order);
        log.info("Order Saved orderId:{}", order.getOrderId());
        return orderEventMapper.entityToEvent(order);
    }
    @Value("${ORDER_CREATED}")
    private String orderCreatedTopic;

    public List<OrderResponse> getAllOrder() {
        return orderMapper.entitiesToResponses(orderRepository.findAll());
    }


    @Transactional
    public String cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Type-safe update using the Enum
        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
        return String.format( "Order %s status updated to CANCELLED", orderId);
    }

    public void updateOrderStatus(Integer orderId, boolean b) {
        Order order = orderRepository.findById(orderId).orElseThrow
                (() -> new OrderNotFound(String.format("The Order Not Found OrderId:%d", orderId)));

        if (b)
            order.setStatus((OrderStatus.COMPLETED));
        else
            order.setStatus(OrderStatus.CANCELLED);

        log.info("The order status changed orderId:{}  status:{}",order.getOrderId(),order.getOrderId());
    }

    @Transactional // Ensures the Order and Outbox entry are committed together
    public void placeOrder(OrderSaveRequest orderSaveRequest) {
        log.info("Placing new order for customer: {}", orderSaveRequest.getCustomerId());

        Order order = orderMapper.requestToEntity(orderSaveRequest);
        orderRepository.save(order);
        log.info("OrderId: {} saved to Db.", order.getOrderId());
        StartOrderPlacedEvent event =orderEventMapper.entityToEvent(order);

        outboxService.saveToOutbox(orderCreatedTopic,event);

        log.info("Order {} placed and inventory check staged in outbox.", order.getOrderId());
    }
}
