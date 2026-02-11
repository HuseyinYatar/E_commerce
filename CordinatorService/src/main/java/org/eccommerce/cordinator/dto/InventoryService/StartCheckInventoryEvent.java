package org.eccommerce.cordinator.dto.InventoryService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StartCheckInventoryEvent {

    private Integer orderId;

    private Integer customerId;

    private BigDecimal totalAmount;

    List<InventoryItemDTO> orderItemDTOS;
}
