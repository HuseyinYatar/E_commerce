package org.eccommerce.cordinator.dto.OrderService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelledEvent {
    // The unique ID of the order to be cancelled
    private Integer orderId;

    // The reason for cancellation (e.g., "Payment failed: Insufficient Funds")
    private String errorMessage;
}