package org.eccommerce.cordinator.dto.PaymentService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StartPaymentEvent {
    private Integer orderId;

    private Integer customerId;

    private BigDecimal totalAmount;
}

