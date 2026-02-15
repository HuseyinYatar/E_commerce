package org.eccommerce.cordinator.dto.PaymentService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentFailedEvent {
    private Integer orderId;
    private String errorMessage;
}

