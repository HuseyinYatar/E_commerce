package org.eccommerce.orderservice.mapper;

import org.eccommerce.orderservice.dto.StartOrderPlacedEvent;
import org.eccommerce.orderservice.dto.OrderSaveRequest;
import org.eccommerce.orderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderEventMapper {

StartOrderPlacedEvent entityToEvent(Order order);
}
