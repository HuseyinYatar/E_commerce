package org.eccommerce.cordinator.mapper;


import org.eccommerce.cordinator.dto.PaymentService.StartPaymentEvent;
import org.eccommerce.cordinator.dto.InventoryService.CheckedInventoryEvent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryMapper {
    StartPaymentEvent toStartPaymentEvent(CheckedInventoryEvent checkedInventoryEvent);
}
