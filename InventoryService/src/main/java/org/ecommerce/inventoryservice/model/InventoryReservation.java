package org.ecommerce.inventoryservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_reservations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class InventoryReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer orderId;    // To find which items to rollback
    private Integer productId;  // The item to be returned to stock
    private Integer quantity; // The amount to be returned
}