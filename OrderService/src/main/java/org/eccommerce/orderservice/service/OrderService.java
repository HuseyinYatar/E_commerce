package org.eccommerce.orderservice.service;


import lombok.extern.slf4j.Slf4j;
import org.eccommerce.orderservice.dto.StartOrderPlacedEvent;
import org.eccommerce.orderservice.dto.OrderResponse;
import org.eccommerce.orderservice.dto.OrderSaveRequest;
import org.eccommerce.orderservice.mapper.OrderEventMapper;
import org.eccommerce.orderservice.mapper.OrderMapper;
import org.eccommerce.orderservice.model.Order;
import org.eccommerce.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderEventMapper orderEventMapper;
    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, OrderEventMapper orderEventMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderEventMapper = orderEventMapper;
    }

    @Transactional
    public StartOrderPlacedEvent OrderPlace(OrderSaveRequest orderSaveRequest)
    {

        Order order = orderMapper.requestToEntity(orderSaveRequest);
        orderRepository.save(order);
        log.info("Order Saved orderId:{}",order.getOrderId());
        return orderEventMapper.entityToEvent(order);
    }


    public List<OrderResponse> getAllOrder()
    {
        return orderMapper.entitiesToResponses(orderRepository.findAll());
    }

}
