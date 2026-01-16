package org.eccommerce.orderservice.controller;


import org.eccommerce.orderservice.dto.OrderResponse;
import org.eccommerce.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
@RestController
@RequestMapping("${ORDER_API}")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("${GET_ALL_ORDER}")
    public List<OrderResponse> getAllOrder()
    {
        return orderService.getAllOrder();
    }
}
