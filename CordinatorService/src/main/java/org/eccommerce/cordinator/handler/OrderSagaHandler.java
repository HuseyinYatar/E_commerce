package org.eccommerce.cordinator.handler;

import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.InventoryService.StartCheckInventoryEvent;
import org.eccommerce.cordinator.dto.OrderService.StartOrderPlacedEvent;
import org.eccommerce.cordinator.mapper.OrderMapper;
import org.eccommerce.cordinator.service.OutboxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OrderSagaHandler {

    private final OutboxService outboxService;
    private final OrderMapper orderMapper;

    @Value("${CHECK_INVENTORY}")
    private String checkInventoryTopic;

    public OrderSagaHandler(OutboxService outboxService, OrderMapper orderMapper) {
        this.outboxService = outboxService;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public void handleOrderPlacement(StartOrderPlacedEvent event) {
        log.info("Saga Step: Preparing Inventory Check for Order: {}", event.getOrderId());

        // 1. Transform the data
        StartCheckInventoryEvent command = orderMapper.START_CHECK_INVENTORY_EVENT(event);

        // 2. Delegate the outbox staging to the specialized service
        outboxService.saveToOutbox(checkInventoryTopic, command);
    }
}