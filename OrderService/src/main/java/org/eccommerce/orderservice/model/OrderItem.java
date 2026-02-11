package org.eccommerce.orderservice.model;


import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class OrderItem {

    private Integer productId;

    private Integer count;

}
