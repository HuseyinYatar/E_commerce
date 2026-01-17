package org.eccommerce.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eccommerce.orderservice.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSaveRequest {
    private Integer orderId;

    private Integer customerId;

    private BigDecimal totalAmount;

    private List<DtoOrderItem> items;
}
