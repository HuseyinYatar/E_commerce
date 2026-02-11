package org.eccommerce.orderservice.mapper;

import org.eccommerce.orderservice.dto.OrdeItemDTO;
import org.eccommerce.orderservice.dto.OrderResponse;
import org.eccommerce.orderservice.dto.OrderSaveRequest;
import org.eccommerce.orderservice.model.Order;
import org.eccommerce.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponse entityToResponse(Order order);

    List<OrderResponse> entitiesToResponses(List<Order> orders);

    Order requestToEntity(OrderSaveRequest orderSaveRequest);

    OrderItem dtoToEntity(OrdeItemDTO ordeItemDTO);
}
