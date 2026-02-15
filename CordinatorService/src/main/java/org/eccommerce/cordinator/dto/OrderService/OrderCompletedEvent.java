package org.eccommerce.cordinator.dto.OrderService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCompletedEvent {
    private Integer orderId;
    private Integer customerId;
}