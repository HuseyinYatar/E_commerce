package org.ecommerce.inventoryservice.mapper;


import org.ecommerce.inventoryservice.dto.CheckedInventoryEvent;
import org.ecommerce.inventoryservice.dto.StartCheckInventoryEvent;
import org.ecommerce.inventoryservice.model.InventoryItem;
import org.ecommerce.inventoryservice.model.InventoryReservation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryMapper {

    CheckedInventoryEvent startEvenToCheckedEvent(StartCheckInventoryEvent startCheckInventoryEvent);
    InventoryReservation toInventoryReservation (InventoryItem inventoryItem);
}
