package org.eccommerce.cordinator.dto.InventoryService;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckedInventoryEvent {

    private Integer orderId;

    private Integer customerId;

    private BigDecimal totalAmount;

}
