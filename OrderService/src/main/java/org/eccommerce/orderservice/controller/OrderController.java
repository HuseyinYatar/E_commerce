package org.eccommerce.orderservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eccommerce.orderservice.dto.OrderResponse;
import org.eccommerce.orderservice.dto.OrderSaveRequest;
import org.eccommerce.orderservice.dto.StartOrderPlacedEvent;
import org.eccommerce.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${ORDER_API}")
@Slf4j
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


    @PostMapping("${ORDER_PLACE}")
    public String placeOrder(@RequestBody OrderSaveRequest orderSaveRequest) {
        log.info("Order save request recieved  orderId:{}", orderSaveRequest.getOrderId());

        StartOrderPlacedEvent startOrderPlacedEvent = orderService.OrderPlace(orderSaveRequest);

        orderService.placeOrder(orderSaveRequest);
        log.info("PlaceOrder event Started  orderId:{}", startOrderPlacedEvent.getOrderId());
        return "Order Placed";
    }

    @GetMapping("${GET_ALL_ORDER}")
    public List<OrderResponse> getAllOrder() {
        return orderService.getAllOrder();
    }

    @PatchMapping("${CANCEL_ORDER_END_POINT}")
    public String cancelOrder(@RequestParam Integer id) {
        return orderService.cancelOrder(id);
    }
}
