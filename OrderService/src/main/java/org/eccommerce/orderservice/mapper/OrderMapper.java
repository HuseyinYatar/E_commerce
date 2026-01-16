package org.eccommerce.orderservice.mapper;

import org.eccommerce.orderservice.dto.OrderResponse;
import org.eccommerce.orderservice.model.Order;
import java.util.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponse entityToResponse(Order order);

    List< OrderResponse> entitiesToResponses(List<Order> orders);
}
