package org.ecommerce.inventoryservice.mapper;


import org.ecommerce.inventoryservice.dto.CheckedInventoryEvent;
import org.ecommerce.inventoryservice.dto.InventoryItemDTO;
import org.ecommerce.inventoryservice.dto.StartCheckInventoryEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryMapper {

    CheckedInventoryEvent  startEvenToCheckedEvent(StartCheckInventoryEvent startCheckInventoryEvent);
}
