package org.eccommerce.cordinator.dto.InventoryService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryFailedEvent {

    private Integer orderId;

    private String errorMessage;
}
