package org.eccommerce.cordinator.dto.InventoryService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eccommerce.cordinator.dto.OrderItemDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StartCheckInventoryEvent {

    List<InventoryItemDTO> orderItemDTOS;
}
