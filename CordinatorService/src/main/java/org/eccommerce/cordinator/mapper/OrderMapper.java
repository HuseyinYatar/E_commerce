package org.eccommerce.cordinator.mapper;


import org.eccommerce.cordinator.dto.InventoryService.InventoryItemDTO;
import org.eccommerce.cordinator.dto.InventoryService.InventoryReverseEvent;
import org.eccommerce.cordinator.dto.InventoryService.StartCheckInventoryEvent;
import org.eccommerce.cordinator.dto.OrderService.OrderCancelledEvent;
import org.eccommerce.cordinator.dto.OrderService.OrderCompletedEvent;
import org.eccommerce.cordinator.dto.OrderService.OrderItemDTO;
import org.eccommerce.cordinator.dto.OrderService.StartOrderPlacedEvent;
import org.eccommerce.cordinator.dto.PaymentService.FinishedPaymentEvent;
import org.eccommerce.cordinator.dto.PaymentService.PaymentFailedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    InventoryItemDTO OrderItemToInventoryEvent(OrderItemDTO orderItemDTO);


    @Mapping(target = "orderItemDTOS",source = "items")
    StartCheckInventoryEvent START_CHECK_INVENTORY_EVENT(StartOrderPlacedEvent startOrderPlacedEvent);

    InventoryReverseEvent TO_INVENTORY_REVERSE_EVENT(PaymentFailedEvent paymentFailedEvent);

    OrderCancelledEvent TO_ORDER_CANCELLED_EVENT(PaymentFailedEvent paymentFailedEvent);

    OrderCompletedEvent TO_ORDER_COMPLETED_EVENT(FinishedPaymentEvent finishedPaymentEvent);
}
