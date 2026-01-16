package org.eccommerce.orderservice.service;


import lombok.extern.slf4j.Slf4j;
import org.eccommerce.orderservice.dto.OrderResponse;
import org.eccommerce.orderservice.mapper.OrderMapper;
import org.eccommerce.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    public List<OrderResponse> getAllOrder()
    {
        return orderMapper.entitiesToResponses(orderRepository.findAll());
    }

}
