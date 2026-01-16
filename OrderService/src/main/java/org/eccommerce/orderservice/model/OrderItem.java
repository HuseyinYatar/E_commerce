package org.eccommerce.orderservice.model;


import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class OrderItem {

    private Integer customerId;

    private Integer productId;

    private BigDecimal fee;

}
