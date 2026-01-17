package org.eccommerce.orderservice.model;


import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class OrderItem {

    private Integer productId;

    private BigDecimal fee;

}
