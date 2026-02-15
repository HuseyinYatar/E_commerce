package org.eccommerce.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_table")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    private Integer customerId;

    private BigDecimal totalAmount;

    @ElementCollection
    @CollectionTable(name ="order_items",joinColumns= @JoinColumn(name = "order_id"))
    private List<OrderItem> items;


    @Enumerated(EnumType.STRING)
    private OrderStatus status;


}
