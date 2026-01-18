package org.eccommerce.cordinator.mapper;


import org.eccommerce.cordinator.dto.InventoryService.InventoryItemDTO;
import org.eccommerce.cordinator.dto.InventoryService.StartCheckInventoryEvent;
import org.eccommerce.cordinator.dto.OrderItemDTO;
import org.eccommerce.cordinator.dto.StartOrderPlacedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    InventoryItemDTO OrderItemToInventoryEvent(OrderItemDTO orderItemDTO);


    @Mapping(target = "orderItemDTOS",source = "items")
    StartCheckInventoryEvent START_CHECK_INVENTORY_EVENT(StartOrderPlacedEvent startOrderPlacedEvent);
}
