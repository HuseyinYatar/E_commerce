package org.ecommerce.inventoryservice.repository;


import org.ecommerce.inventoryservice.model.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Integer> {
    List<InventoryReservation> findAllByOrderId(Integer orderId);

    void deleteByOrderId(Integer orderId);


}
