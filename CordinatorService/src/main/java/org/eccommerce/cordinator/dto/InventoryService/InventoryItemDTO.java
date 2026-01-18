package org.eccommerce.cordinator.dto.InventoryService;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryItemDTO {
    private Integer productId;
    private Integer count;

}
